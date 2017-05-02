#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

#
# Implementation for the Property class
#

#######################################################################
from cdb.common.db.api.propertyDbApi import PropertyDbApi
from cdb.common.objects.cdbObjectManager import CdbObjectManager
import ast


class PropertyControllerImpl(CdbObjectManager):
    def __init__(self):
        CdbObjectManager.__init__(self)
        self.propertyDbApi = PropertyDbApi()

    def getPropertyTypes(self):
        return self.propertyDbApi.getPropertyTypes()

    def getPropertyType(self, propertyTypeId):
        return self.propertyDbApi.getPropertyTypeById(propertyTypeId)

    def getAllowedPropertyValueList(self, propertyTypeId):
        return self.propertyDbApi.getAllowedPropertyValuesForPropertyType(propertyTypeId)

    def getPropertyMetadataForPropertyValueId(self, propertyValueId):
        return self.propertyDbApi.getPropertyMetadataForPropertyValueId(propertyValueId)

    def addPropertyMetadataForPropertyValueId(self, propertyValueId, metadataKey, metadataValue, userId):
        return self.propertyDbApi.addPropertyMetadataForPropertyValueId(propertyValueId, metadataKey, metadataValue, userId)

    def addPropertyValueMetadataFromDict(self, propertyValueId, propertyValueMetadataKeyValueDictStringRep, userId):
        propertyValueMetadataKeyValueDict = ast.literal_eval(propertyValueMetadataKeyValueDictStringRep)

        return self.propertyDbApi.addPropertyValueMetadataFromDict(propertyValueId, propertyValueMetadataKeyValueDict, userId)
