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

class LogControllerImpl(CdbObjectManager):
    def __init__(self):
        CdbObjectManager.__init__(self)
        self.logDbApi = LogDbApi()

    def addLogAttachment(self, logId, attachmentName, attachmentTag, attachmentDescription):
        return self.logDbApi.addLogAttachment(logId, attachmentName, attachmentTag, attachmentDescription)

    def getLogById(self, id):
        return self.logDbApi.getLogById(id)
