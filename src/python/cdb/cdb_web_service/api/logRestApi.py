#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.utility.encoder import Encoder
from cdb.common.objects.logAttachment import LogAttachment
from cdb.common.objects.log import Log
from cdb.common.objects.cdbObject import CdbObject
from cdb.common.api.cdbRestApi import CdbRestApi


class LogRestApi(CdbRestApi):
    def __init__(self, username=None, password=None, host=None, port=None, protocol=None):
        CdbRestApi.__init__(self, username, password, host, port, protocol)

    def addLogAttachment(self, logId, attachment, attachmentDescription = None):
        if logId is not None:
            logId = str(logId)
        if logId is None or not len(logId):
            raise InvalidRequest('Log id must be provided.')
        if attachment is None or len(attachment) == 0:
            raise InvalidRequest('Attachment must be specified.')

        url = '%s/logs/%s/addAttachment' % (self.getContextRoot(), logId)

        # Add attachment information
        fileName, data = self._generateFileData(attachment)
        url += '?attachmentName=%s' % Encoder.encode(fileName)

        if attachmentDescription is not None and len(attachmentDescription) > 0:
            url += '&attachmentDescription=%s' % Encoder.encode(attachmentDescription)

        responseDict = self.sendSessionRequest(url=url, method='POST', data=data)

        return LogAttachment(responseDict)

    def updateLogEntry(self, logId, text=None, effectiveFromDateTime=None, effectiveToDateTime=None, logTopicName=None):
        if logId is not None:
            logId = str(logId)
        if logId is None or not len(logId):
            raise InvalidRequest('Log id must be provided.')

        url = '%s/logs/%s/update' % (self.getContextRoot(), logId)

        if text is not None:
            text = Encoder.encode(text)
            url = self._appendUrlParameter(url, 'text', text)
            
        if effectiveFromDateTime is not None:
            effectiveFromDateTime = Encoder.encode(effectiveFromDateTime)
            url = self._appendUrlParameter(url, 'effectiveFromDateTime', effectiveFromDateTime)
        
        if effectiveToDateTime is not None:
            effectiveToDateTime = Encoder.encode(effectiveToDateTime)
            url = self._appendUrlParameter(url, 'effectiveToDateTime', effectiveToDateTime)

        if logTopicName is not None:
            logTopicName = Encoder.encode(logTopicName)
            url = self._appendUrlParameter(url, 'logTopicName', logTopicName)

        responseDict = self.sendSessionRequest(url=url, method='PUT')

        return Log(responseDict)

    def deleteLogEntry(self, logId):
        if logId is not None:
            logId = str(logId)
        if logId is None or not len(logId):
            raise InvalidRequest('Log id must be provided.')

        url = '%s/logs/%s/delete' % (self.getContextRoot(), logId)

        response = self.sendSessionRequest(url=url, method='DELETE')
        return CdbObject(response)