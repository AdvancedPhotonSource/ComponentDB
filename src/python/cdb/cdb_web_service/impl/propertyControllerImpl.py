#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

#
# Implementation for the Property class
#

#######################################################################
import json

from cdb.common.db.api.propertyDbApi import PropertyDbApi
from cdb.common.objects.cdbObjectManager import CdbObjectManager
from cdb.common.utility.encoder import Encoder
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
        propertyValueMetadataKeyValueDict = json.loads(propertyValueMetadataKeyValueDictStringRep)
        return self.propertyDbApi.addPropertyValueMetadataFromDict(propertyValueId, propertyValueMetadataKeyValueDict, userId)

    def packageOptionalPropertyValueVariables(self, tag=None, value=None, units=None, description=None, isUserWriteable=None, isDynamic=None, displayValue=None):
        optionalParameters = {}

        if tag is not None:
            tag = Encoder.decode(tag)
            optionalParameters.update({'tag': tag})

        if value is not None:
            value = Encoder.decode(value)
            optionalParameters.update({'value': value})

        if displayValue is not None:
            displayValue = Encoder.decode(displayValue)
            optionalParameters.update({'displayValue': displayValue})

        if units is not None:
            units = Encoder.decode(units)
            optionalParameters.update({'units': units})

        if description is not None:
            description = Encoder.decode(description)
            optionalParameters.update({'description': description})

        if isUserWriteable is not None:
            isUserWriteable = eval(isUserWriteable)
            optionalParameters.update({'isUserWriteable': isUserWriteable})

        if isDynamic is not None:
            isDynamic = eval(isDynamic)
            optionalParameters.update({'isDynamic', isDynamic})

        return optionalParameters
