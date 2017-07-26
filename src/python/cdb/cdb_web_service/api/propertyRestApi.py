#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.utility.encoder import Encoder
from cdb.common.objects.propertyMetadata import PropertyMetadata
from cdb.common.objects.propertyType import PropertyType
from cdb.common.objects.allowedPropertyValue import AllowedPropertyValue
from cdb.common.api.cdbRestApi import CdbRestApi


class PropertyRestApi(CdbRestApi):
    def __init__(self, username=None, password=None, host=None, port=None, protocol=None):
        CdbRestApi.__init__(self, username, password, host, port, protocol)

    def addPropertyMetadataToPropertyValue(self, propertyValueId, metadataKey = None, metadataValue = None, metadataDict = None):
        if propertyValueId is not None:
            propertyValueId = str(propertyValueId)
        if propertyValueId is None or not len(propertyValueId):
            raise InvalidRequest("propertyValueId must be provided")

        url = '%s/property/values/%s/addMetadata' % (self.getContextRoot(), propertyValueId)

        list = False

        if metadataKey is None or not len(metadataKey):
            if metadataDict is None or not len(metadataDict):
                raise InvalidRequest("metadataKey and value or metadataDict must be provided")
            # use metadata dict
            url += '?metadataDict=%s' % Encoder.encode(metadataDict)
            list = True
        else:
            if metadataValue is None or not len(metadataValue):
                raise InvalidRequest("metadataValue must be provided")

            url = '%s/%s' % (url, metadataKey)

            url += '?metadataValue=%s' % Encoder.encode(metadataValue)

        response = self.sendSessionRequest(url=url, method='POST')

        if list:
            return self.toCdbObjectList(response, PropertyMetadata)
        else:
            return PropertyMetadata(response)

    def getPropertyTypes(self):
        url = '%s/property/types' % self.getContextRoot()
        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, PropertyType)
    
    def getPropertyType(self, propertyTypeId):
        if propertyTypeId is not None:
            propertyTypeId = str(propertyTypeId)
        if propertyTypeId is None or not len(propertyTypeId):
            raise InvalidRequest("propertyTypeId must be provided")

        url = '%s/property/types/%s' % (self.getContextRoot(), propertyTypeId)

        responseData = self.sendRequest(url=url, method='GET')
        return PropertyType(responseData)

    def getAllowedPropertyValuesForPropertyType(self, propertyTypeId):
        if propertyTypeId is not None:
            propertyTypeId = str(propertyTypeId)
        if propertyTypeId is None or not len(propertyTypeId):
            raise InvalidRequest("propertyTypeId must be provided")

        url = '%s/property/types/%s/allowedPropertyValues' % (self.getContextRoot(), propertyTypeId)

        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, AllowedPropertyValue)
    
    def getPropertyMetadataForPropertyValue(self, propertyValueId):
        if propertyValueId is not None:
            propertyValueId = str(propertyValueId)
        if propertyValueId is None or not len(propertyValueId):
            raise InvalidRequest("propertyValueId must be provided")

        url = '%s/property/values/%s/metadata' % (self.getContextRoot(), propertyValueId)

        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, PropertyMetadata)
