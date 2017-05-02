#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import cherrypy
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.cdb_web_service.impl.itemControllerImpl import ItemControllerImpl
from cdb.common.service.cdbSessionController import CdbSessionController
from cdb.common.utility.encoder import Encoder


class ItemSessionController(CdbSessionController):

    def __init__(self):
        CdbSessionController.__init__(self)
        self.itemControllerImpl = ItemControllerImpl()

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
    def addPropertyValueToItemByItemId(self, itemId, propertyTypeName):
        if not itemId:
            raise InvalidRequest("Invalid itemId provided")
        if not propertyTypeName:
            raise InvalidRequest("Invalid propertyTypeName provided")

        sessionUser = self.getSessionUser()
        enteredByUserId = sessionUser.get('id')

        itemElementPropertyValueAdded = self.itemControllerImpl.addPropertyValueForItemWithId(itemId, propertyTypeName, enteredByUserId)
        propertyValueAdded = itemElementPropertyValueAdded.data['propertyValue']

        response = propertyValueAdded.getFullJsonRep()
        self.logger.debug('Returning new property value created for item with id %s: %s' % (itemId, propertyValueAdded))
        return response

