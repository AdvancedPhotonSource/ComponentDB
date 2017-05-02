#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.utility.encoder import Encoder
from cdb.common.objects.log import Log
from cdb.common.objects.propertyValue import PropertyValue
from cdb.common.api.cdbRestApi import CdbRestApi

class ItemRestApi(CdbRestApi):
    def __init__(self, username=None, password=None, host=None, port=None, protocol=None):
        CdbRestApi.__init__(self, username, password, host, port, protocol)

    def addLogEntryToItemWithQrId(self, qrId, logEntry, attachment):
        if qrId is None or not len(qrId):
            raise InvalidRequest("QrId must be provided")

        url = '%s/items/%s/addLogEntry' % (self.getContextRoot(), qrId)

        if logEntry is None or not len(logEntry):
            raise InvalidRequest('Log entry must be provided.')

        url += '?logEntry=%s' % Encoder.encode(logEntry)

        if attachment is not None and len(attachment) > 0:
            fileName, data = self._generateFileData(attachment)
            url += '&attachmentName=%s' % Encoder.encode(fileName)
            responseDict = self.sendSessionRequest(url=url, method='POST', data=data)
        else:
            responseDict = self.sendSessionRequest(url=url, method='POST')

        return Log(responseDict)
    
    def addPropertyValueToItemwithId(self, itemId, propertyTypeName):
        if itemId is None or not len(itemId):
            raise InvalidRequest("itemId must be provided")
        if propertyTypeName is None or not len(propertyTypeName):
            raise InvalidRequest("propertyTypeName must be provided")

        url = '%s/items/%s/addPropertyValue/%s' % (self.getContextRoot(), itemId, propertyTypeName)

        responseDict = self.sendSessionRequest(url=url, method='POST')

        return PropertyValue(responseDict)

    def getLogEntriesForItemWithQrId(self, qrId):
        if qrId is None or not len(qrId):
            raise InvalidRequest("QrId must be provided")

        url = '%s/items/%s/logs' % (self.getContextRoot(), qrId)

        responseData = self.sendRequest(url, method='GET')
        return self.toCdbObjectList(responseData, Log)