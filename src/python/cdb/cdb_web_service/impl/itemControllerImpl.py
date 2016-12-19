#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Implementation for the Item class
#

#######################################################################
from cdb.common.db.api.logDbApi import LogDbApi
from cdb.common.objects.cdbObjectManager import CdbObjectManager
from cdb.common.db.api.itemDbApi import ItemDbApi
from cdb.common.utility.storageUtility import StorageUtility


class ItemControllerImpl(CdbObjectManager):
    def __init__(self):
        CdbObjectManager.__init__(self)
        self.itemDbApi = ItemDbApi()
        self.logDbApi = LogDbApi()
        self.storageUtility = StorageUtility.getInstance()

    def getItemById(self, itemId):
        return self.itemDbApi.getItemById(itemId)

    def addLogEntryForItemWithQrId(self, qrId, logEntryText, enteredByUserId, attachmentName, cherryPyData):
        item = self.itemDbApi.getItemByQrId(qrId)

        itemId = item.data['id']
        selfElement = self.itemDbApi.getSelfElementByItemId(itemId)
        selfElementId = selfElement.data['id']

        # Raises exception if user does not have sufficient privilages
        self.itemDbApi.verifyPermissionsForWriteToItemElement(enteredByUserId, selfElementId)

        # Check if attachment needs to be stored
        storedAttachmentName = None
        if attachmentName is not None and len(attachmentName) > 0:
            storedAttachmentName = self.storageUtility.storeLogAttachment(cherryPyData, attachmentName)

        itemElementLog = self.itemDbApi.addItemElementLog(selfElementId, logEntryText, enteredByUserId)
        logEntry = itemElementLog.data['log']

        if storedAttachmentName is not None:
            logId = logEntry.data['id']
            logAttachment = self.logDbApi.addLogAttachment(logId, storedAttachmentName, attachmentName, None)
            del(logAttachment.data['log'])
            logAttachmentJsonRep = logAttachment.getFullJsonRep()
            # Refresh log entry
            logEntry.data['logAttachmentAdded'] = logAttachmentJsonRep

        return logEntry
