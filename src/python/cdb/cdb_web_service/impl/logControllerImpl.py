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

    def addLogAttachment(self, logId, attachmentName, attachmentDescription, cherryPyData):
        storedAttachmentName = self.storageUtility.storeLogAttachment(cherryPyData, attachmentName)
        return self.logDbApi.addLogAttachment(logId, storedAttachmentName, attachmentName, attachmentDescription)

    def getLogById(self, id):
        return self.logDbApi.getLogById(id)
