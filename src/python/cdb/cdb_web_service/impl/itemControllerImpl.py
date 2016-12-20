#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Implementation for the Item class
#

#######################################################################
from cdb.cdb_web_service.impl.logControllerImpl import LogControllerImpl
from cdb.common.objects.cdbObjectManager import CdbObjectManager
from cdb.common.db.api.itemDbApi import ItemDbApi


class ItemControllerImpl(CdbObjectManager):
    def __init__(self):
        CdbObjectManager.__init__(self)
        self.itemDbApi = ItemDbApi()
        self.logControllerImpl = LogControllerImpl()

    def getItemById(self, itemId):
        return self.itemDbApi.getItemById(itemId)

    def addLogEntryForItemWithQrId(self, qrId, logEntryText, enteredByUserId, attachmentName, cherryPyData):
        item = self.itemDbApi.getItemByQrId(qrId)

        itemId = item.data['id']
        selfElement = self.itemDbApi.getSelfElementByItemId(itemId)
        selfElementId = selfElement.data['id']

        # Add log entry
        itemElementLog = self.itemDbApi.addItemElementLog(selfElementId, logEntryText, enteredByUserId)
        logEntry = itemElementLog.data['log']

        # Check if log has an attachment that needs to be stored
        if attachmentName is not None and len(attachmentName) > 0:
            logId = logEntry.data['id']
            logAttachment = self.logControllerImpl.addLogAttachment(logId, attachmentName, None, cherryPyData)
            del(logAttachment.data['log'])
            logAttachmentJsonRep = logAttachment.getFullJsonRep()
            logEntry.data['logAttachmentAdded'] = logAttachmentJsonRep

        return logEntry
