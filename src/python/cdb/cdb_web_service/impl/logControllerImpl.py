#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

#
# Implementation for the Log class
#

#######################################################################

from cdb.common.objects.cdbObjectManager import CdbObjectManager
from cdb.common.db.api.logDbApi import LogDbApi
from cdb.common.utility.storageUtility import StorageUtility


class LogControllerImpl(CdbObjectManager):
    def __init__(self):
        CdbObjectManager.__init__(self)
        self.logDbApi = LogDbApi()
        self.storageUtility = StorageUtility.getInstance()

    def addLogAttachment(self, logId, attachmentName, attachmentDescription, attachmentAddedByUserId, cherryPyData):
        # Verify user will have permission to add log attachment before saving attachment.
        self.logDbApi.verifyUserCreatedLogEntry(attachmentAddedByUserId, logId)

        storedAttachmentName = self.storageUtility.storeLogAttachment(cherryPyData, attachmentName)
        return self.logDbApi.addLogAttachment(logId, storedAttachmentName, attachmentName, attachmentDescription,
                                              attachmentAddedByUserId)

    def getLogById(self, id):
        return self.logDbApi.getLogById(id)

    def getLogEntriesForItemElement(self, itemElementId):
        return self.logDbApi.getLogEntriesForItemElementId(itemElementId)

    def updateLogEntry(self, logId, enteredByUserId, text, effectiveFromDateTime,
                       effectiveToDateTime, logTopicName):
        return self.logDbApi.updateLogEntry(logId, enteredByUserId, text, effectiveFromDateTime, effectiveToDateTime,
                                            logTopicName)

    def deleteLogEntry(self, logId, userId):
        self.logDbApi.deleteLogEntry(logId, userId)
