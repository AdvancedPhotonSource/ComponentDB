#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import cherrypy

from cdb.cdb_web_service.impl.propertyControllerImpl import PropertyControllerImpl
from cdb.common.service.cdbController import CdbController
from cdb.common.exceptions.invalidRequest import InvalidRequest

class PropertyController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.propertyControllerImpl = PropertyControllerImpl();


    @cherrypy.expose
    @CdbController.execute
    def getPropertyTypes(self):
        propertyTypes = self.propertyControllerImpl.getPropertyTypes()
        response = self.listToJson(propertyTypes)

        self.logger.debug('Returning Property Types')
        return response

    @cherrypy.expose
    @CdbController.execute
    def getPropertyType(self, propertyTypeId):
        if not propertyTypeId:
            raise InvalidRequest("Invalid propertyTypeId provided")

        propertyType = self.propertyControllerImpl.getPropertyType(propertyTypeId)
        response = propertyType.getFullJsonRep()

        self.logger.debug('Returning Property Type with id %s' % propertyTypeId)
        return response

    @cherrypy.expose
    @CdbController.execute
    def getAllowedPropertyValuesForPropertyType(self, propertyTypeId):
        if not propertyTypeId:
            raise InvalidRequest("Invalid propertyTypeId provided")

        allowedPropertyValueList = self.propertyControllerImpl.getAllowedPropertyValueList(propertyTypeId)
        response = self.listToJson(allowedPropertyValueList)

        self.logger.debug('Returning Allowed Property Values for Type with id %s' % propertyTypeId)
        return response

    @cherrypy.expose
    @CdbController.execute
    def getPropertyMetadataForPropertyValueId(self, propertyValueId):
        if not propertyValueId:
            raise InvalidRequest("Invalid propertyValueId provided")

        propertyMetadataList = self.propertyControllerImpl.getPropertyMetadataForPropertyValueId(propertyValueId)

        response = self.listToJson(propertyMetadataList)
        self.logger.debug('Returning Metadata for property Value id %s' % propertyValueId)

        return response