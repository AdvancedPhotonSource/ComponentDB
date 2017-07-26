#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import cherrypy

from cdb.cdb_web_service.impl.propertyControllerImpl import PropertyControllerImpl
from cdb.common.service.cdbController import CdbController
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.service.cdbSessionController import CdbSessionController
from cdb.common.utility.encoder import Encoder


class PropertySessionController(CdbSessionController):

    def __init__(self):
        CdbController.__init__(self)
        self.propertyControllerImpl = PropertyControllerImpl();

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def addPropertyValueMetadata(self, propertyValueId, metadataKey, metadataValue):
        if not propertyValueId:
            raise InvalidRequest("Invalid propertyValueId provided")
        if not metadataKey:
            raise InvalidRequest("Invalid metadataKey provided")
        if not metadataValue:
            raise InvalidRequest("Invalid metadataValue provided")

        metadataValue = Encoder.decode(metadataValue)

        sessionUser = self.getSessionUser()
        enteredByUserId = sessionUser.get('id')

        addedMetadata = self.propertyControllerImpl.addPropertyMetadataForPropertyValueId(propertyValueId, metadataKey, metadataValue, enteredByUserId)
        response = addedMetadata.getFullJsonRep()

        self.logger.debug('Returning Property Metadata for property value with id %s: %s' % (propertyValueId,addedMetadata))
        return response

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def addPropertyValueMetadataFromDict(self, propertyValueId, metadataDict):
        if not propertyValueId:
            raise InvalidRequest("Invalid propertyValueId provided")
        if not metadataDict:
            raise InvalidRequest("Invalid propertyMetadataDict provided")

        propertyMetadataDictStringRep = Encoder.decode(metadataDict)

        sessionUser = self.getSessionUser()
        enteredByUserId = sessionUser.get('id')

        addedUpdatedPropertyMetadata = self.propertyControllerImpl.addPropertyValueMetadataFromDict(propertyValueId, propertyMetadataDictStringRep, enteredByUserId)

        response = self.listToJson(addedUpdatedPropertyMetadata)

        self.logger.debug("Returning property metadata for property value with id %s: %s" % (propertyValueId, response))

        return response

