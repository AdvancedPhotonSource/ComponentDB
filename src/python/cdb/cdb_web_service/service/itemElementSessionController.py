#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import cherrypy
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.cdb_web_service.impl.itemElementControllerImpl import ItemElementControllerImpl
from cdb.cdb_web_service.impl.propertyControllerImpl import PropertyControllerImpl
from cdb.common.service.cdbSessionController import CdbSessionController
from cdb.common.utility.encoder import Encoder


class ItemElementSessionController(CdbSessionController):

    def __init__(self):
        CdbSessionController.__init__(self)
        self.itemElementImplController = ItemElementControllerImpl()
        self.propertyControllerImpl = PropertyControllerImpl()

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def addItemElement(self, parentItemId, itemElementName, containedItemId=None, description=None,
                       ownerUserId=None, ownerGroupId=None, isRequired=None, isGroupWriteable=None):
        sessionUser = self.getSessionUser()
        createdByUserId = sessionUser.get('id')

        if ownerUserId is None:
            ownerUserId = createdByUserId

        if ownerGroupId is None:
            userGroupList = sessionUser.data['userGroupList']
            if len(userGroupList) > 0:
                ownerGroupId = userGroupList[0].data['id']
            else:
                raise InvalidRequest(
                    "Invalid, current session user is not assigned to any groups... please specify owner group id.")

        optionalParameters = {}

        itemElementName = Encoder.decode(itemElementName)

        if containedItemId is not None:
            optionalParameters.update({'containedItemId': containedItemId})

        if description is not None:
            description = Encoder.decode(description)
            optionalParameters.update({'description': description})

        if isRequired is not None:
            isRequired = eval(isRequired)
            optionalParameters.update({'isRequired': isRequired})

        if isGroupWriteable is not None:
            isGroupWriteable = eval(isGroupWriteable)
            optionalParameters.update({'isGroupWriteable': isGroupWriteable})

        response = self.itemElementImplController.addItemElement(parentItemId, itemElementName, createdByUserId, ownerUserId, ownerGroupId, **optionalParameters)
        self.logger.debug('Returning new item element: %s' % (response))
        return response.getFullJsonRep()

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def addPropertyValueToItemElementById(self, itemElementId, propertyTypeName, tag=None, value=None, units=None,
                                       description=None,
                                       isUserWriteable=None, isDynamic=None):
        if not itemElementId:
            raise InvalidRequest("Invalid itemElementId provided")
        if not propertyTypeName:
            raise InvalidRequest("Invalid propertyTypeName provided")

        propertyTypeName = Encoder.decode(propertyTypeName)

        sessionUser = self.getSessionUser()
        enteredByUserId = sessionUser.get('id')

        optionalParameters = self.propertyControllerImpl.packageOptionalPropertyValueVariables(tag, value,
                                                                                               units, description,
                                                                                               isUserWriteable,
                                                                                               isDynamic)

        itemElementPropertyValueAdded = self.itemElementImplController.addPropertyValueForItemElementWithId(itemElementId, propertyTypeName,
                                                                                              enteredByUserId,
                                                                                              **optionalParameters)
        propertyValueAdded = itemElementPropertyValueAdded.data['propertyValue']

        response = propertyValueAdded.getFullJsonRep()
        self.logger.debug('Returning new property value created for item element with id %s: %s' % (itemElementId, propertyValueAdded))
        return response
