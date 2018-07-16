#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import cherrypy
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.cdb_web_service.impl.itemControllerImpl import ItemControllerImpl
from cdb.cdb_web_service.impl.propertyControllerImpl import PropertyControllerImpl
from cdb.common.service.cdbSessionController import CdbSessionController
from cdb.common.utility.encoder import Encoder


class ItemSessionController(CdbSessionController):

    def __init__(self):
        CdbSessionController.__init__(self)
        self.itemControllerImpl = ItemControllerImpl()
        self.propertyControllerImpl = PropertyControllerImpl()

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def addLogToItemByQrId(self, qrId, logEntry, attachmentName=None, **kwargs):
        if not qrId:
            raise InvalidRequest("Invalid qrId provided")
        if not logEntry:
            raise InvalidRequest("Log entry must be provided")

        sessionUser = self.getSessionUser()
        enteredByUserId = sessionUser.get('id')
        attachmentName = Encoder.decode(attachmentName)
        cherrypyData = cherrypy.request.body
        logEntry = Encoder.decode(logEntry)

        logAdded = self.itemControllerImpl.addLogEntryForItemWithQrId(qrId, logEntry, enteredByUserId, attachmentName, cherrypyData)

        response = logAdded.getFullJsonRep()
        self.logger.debug('Returning log info for item with qrid %s: %s' % (qrId, response))
        return response

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def addPropertyImageToItem(self, itemId, imageFileName):
        if not itemId:
            raise InvalidRequest("Invalid item id provided")
        if not imageFileName:
            raise InvalidRequest("Invalid image file name provided")

        sessionUser = self.getSessionUser()
        enteredByUserId = sessionUser.get('id')
        imageFileName = Encoder.decode(imageFileName)
        cherrypyData = cherrypy.request.body

        imagePropertyAdded = self.itemControllerImpl.addPropertyImageToItem(itemId, imageFileName, enteredByUserId, cherrypyData)

        return imagePropertyAdded.getFullJsonRep()

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def addItemElementRelationship(self, relationshipTypeName, firstItemId=None, secondItemId=None,
                                   firstItemQrId=None, secondItemQrId=None,
                                   relationshipDetails=None, description=None):
        if not firstItemId and not firstItemQrId:
            raise InvalidRequest("Invalid first item id provided")
        if not secondItemId and not secondItemQrId:
            raise InvalidRequest("Invalid second item id provided")
        if not relationshipTypeName:
            raise InvalidRequest("Invalid relationship type name provided")

        usingQrIds = None
        if firstItemId and secondItemId:
            usingQrIds = False
        elif firstItemQrId and secondItemQrId:
            usingQrIds = True
        else:
            raise InvalidRequest("Must either specify both qrIds or both Ids")

        relationshipTypeName = Encoder.decode(relationshipTypeName)

        if relationshipDetails is not None:
            relationshipDetails = Encoder.decode(relationshipDetails)
        if description is not None:
            description = Encoder.decode(description)

        sessionUser = self.getSessionUser()
        enteredByUserId = sessionUser.get('id')

        logMessageStart = "Returning item element relationship between "

        if usingQrIds:
            itemElementRelationship = self.itemControllerImpl.addItemElementRelationshipByQrId(firstItemQrId, secondItemQrId,
                                                                                               relationshipTypeName,
                                                                                               enteredByUserId,
                                                                                               relationshipDetails,
                                                                                               description)

            logMessageStart += "item qr: %s and item qr: %s" % (firstItemQrId, secondItemQrId)
        else:
            itemElementRelationship = self.itemControllerImpl.addItemElementRelationship(firstItemId, secondItemId,
                                                                                         relationshipTypeName,
                                                                                         enteredByUserId,
                                                                                         relationshipDetails, description)

            logMessageStart += "item id: %s and item id: %s" % (firstItemId, secondItemId)


        response = itemElementRelationship.getFullJsonRep()

        self.logger.debug("%s of type: %s : %s" %
                          (logMessageStart, relationshipTypeName, response))

        return response

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def addPropertyValueToItemByItemId(self, itemId, propertyTypeName, tag=None, value=None, units=None, description=None,
                                      isUserWriteable=None, isDynamic=None):
        if not itemId:
            raise InvalidRequest("Invalid itemId provided")
        if not propertyTypeName:
            raise InvalidRequest("Invalid propertyTypeName provided")

        propertyTypeName = Encoder.decode(propertyTypeName)

        sessionUser = self.getSessionUser()
        enteredByUserId = sessionUser.get('id')

        optionalParameters = self.propertyControllerImpl.packageOptionalPropertyValueVariables(tag, value,
                                                                                               units, description,
                                                                                               isUserWriteable, isDynamic)

        itemElementPropertyValueAdded = self.itemControllerImpl.addPropertyValueForItemWithId(itemId, propertyTypeName,
                                                                                              enteredByUserId,
                                                                                              **optionalParameters)
        propertyValueAdded = itemElementPropertyValueAdded.data['propertyValue']

        response = propertyValueAdded.getFullJsonRep()
        self.logger.debug('Returning new property value created for item with id %s: %s' % (itemId, propertyValueAdded))
        return response

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def addItem(self, domainName, name, itemProjectName=None, ownerUserId=None, ownerGroupId=None, itemIdentifier1=None, itemIdentifier2=None,
                qrId=None, description=None, isGroupWriteable=None, entityTypeNames = None, derivedFromItemId=None):
        if not domainName:
            raise InvalidRequest("Invalid domain name provided")
        if not name:
            raise InvalidRequest("Invalid item name provided")

        domainName = Encoder.decode(domainName)
        name = Encoder.decode(name)

        sessionUser = self.getSessionUser()
        createdByUserId = sessionUser.get('id')

        if ownerUserId is None:
            ownerUserId = createdByUserId

        if ownerGroupId is None:
            userGroupList = sessionUser.data['userGroupList']
            if len(userGroupList) > 0:
                ownerGroupId = userGroupList[0].data['id']
            else:
                raise InvalidRequest("Invalid, current session user is not assigned to any groups... please specify owner group id.")

        optionalParameters = {}

        if itemIdentifier1 is not None:
            itemIdentifier1 = Encoder.decode(itemIdentifier1)
            optionalParameters.update({'itemIdentifier1': itemIdentifier1})

        if itemIdentifier2 is not None:
            itemIdentifier2 = Encoder.decode(itemIdentifier2)
            optionalParameters.update({'itemIdentifier2': itemIdentifier2})
            
        if qrId is not None:
            optionalParameters.update({'qrId': qrId})
            
        if description is not None:
            description = Encoder.decode(description)
            optionalParameters.update({'description': description})
            
        if isGroupWriteable is not None:
            isGroupWriteable = eval(isGroupWriteable)
            optionalParameters.update({'isGroupWriteable': isGroupWriteable})

        if entityTypeNames is not None:
            entityTypeNames = Encoder.decode(entityTypeNames)
            if entityTypeNames[0] == '[':
                entityTypeNames = eval(entityTypeNames)
            optionalParameters.update({'entityTypeNames': entityTypeNames})

        if derivedFromItemId is not None:
            optionalParameters.update({'derivedFromItemId': derivedFromItemId})

        if itemProjectName is not None:
            itemProjectName = Encoder.decode(itemProjectName)
            optionalParameters.update({'itemProjectName': itemProjectName})

        response = self.itemControllerImpl.addItem(domainName, name, createdByUserId, ownerUserId, ownerGroupId, **optionalParameters)
        self.logger.debug('Returning new item: %s' % (response))
        return response.getFullJsonRep()

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def updateInventoryItemStatus(self, itemId, status):
        if not itemId:
            raise InvalidRequest("Item id must be provided")
        if not status:
            raise InvalidRequest("Status must be provided")

        status = Encoder.decode(status)

        sessionUser = self.getSessionUser()
        enteredByUserId = sessionUser.get('id')

        response = self.itemControllerImpl.updateInventoryItemStatus(itemId, status, enteredByUserId)
        self.logger.debug("Returning updated status: %s" %(response))

        return response.getFullJsonRep()

