#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.utility.encoder import Encoder
from cdb.common.objects.propertyMetadata import PropertyMetadata
from cdb.common.api.cdbRestApi import CdbRestApi

class PropertyRestApi(CdbRestApi):
    def __init__(self, username=None, password=None, host=None, port=None, protocol=None):
        CdbRestApi.__init__(self, username, password, host, port, protocol)

    def addPropertyMetadataToPropertyValue(self, propertyValueId, metadataKey = None, metadataValue = None, metadataDict = None):
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