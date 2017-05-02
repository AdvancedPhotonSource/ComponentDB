#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import cherrypy

from cdb.cdb_web_service.service.propertyController import PropertyController
from cdb.cdb_web_service.service.propertySessionController import PropertySessionController
from cdb.common.utility.configurationManager import ConfigurationManager


class PropertyRouteDescriptor:

    @classmethod
    def getRoutes(cls):
        contextRoot = ConfigurationManager.getInstance().getContextRoot()

        # Static instances shared between different routes
        propertyController = PropertyController()
        propertySessionController = PropertySessionController()

        routes = [
            # Get property types
            {
                'name': 'getPropertyTypes',
                'path': '%s/property/types' % contextRoot,
                'controller': propertyController,
                'action': 'getPropertyTypes',
                'method': ['GET']
            },
            {
                'name': 'getPropertyType',
                'path': '%s/property/types/:(propertyTypeId)' % contextRoot,
                'controller': propertyController,
                'action': 'getPropertyType',
                'method': ['GET']
            },
            {
                'name': 'getAllowedPropertyValuesForPropertyType',
                'path': '%s/property/types/:(propertyTypeId)/allowedPropertyValues' % contextRoot,
                'controller': propertyController,
                'action': 'getAllowedPropertyValuesForPropertyType',
                'method': ['GET']
            },
            {
                'name': 'getPropertyMetadataForPropertyValue',
                'path': '%s/property/values/:(propertyValueId)/metadata' % contextRoot,
                'controller': propertyController,
                'action': 'getPropertyMetadataForPropertyValueId',
                'method': ['GET']
            },
            {
                'name': 'addPropertyMetadataForPropertyValue',
                'path': '%s/property/values/:(propertyValueId)/addMetadata/:(metadataKey)' % contextRoot,
                'controller': propertySessionController,
                'action': 'addPropertyValueMetadata',
                'method': ['POST']
            },
            {
                'name': 'addPropertyMetadataForPropertyValueUsingDict',
                'path': '%s/property/values/:(propertyValueId)/addMetadata' % contextRoot,
                'controller': propertySessionController,
                'action': 'addPropertyValueMetadataFromDict',
                'method': ['POST']
            },
        ]

        return routes


