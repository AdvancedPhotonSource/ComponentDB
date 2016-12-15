#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
import os
import shutil

import cherrypy
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.cdb_web_service.impl.itemControllerImpl import ItemControllerImpl
from cdb.cdb_web_service.impl.logControllerImpl import LogControllerImpl
from cdb.common.service.cdbSessionController import CdbSessionController
from cdb.common.utility.encoder import Encoder
from cdb.common.utility.storageUtility import StorageUtility


class ItemSessionController(CdbSessionController):

    def __init__(self):
        CdbSessionController.__init__(self)
        self.itemControllerImpl = ItemControllerImpl()
        self.logControllerImpl = LogControllerImpl()
        self.storageUtility = StorageUtility.getInstance()

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def addLogToItemByQrId(self, qrId, logEntry, attachmentName=None, **kwargs):
        if not qrId:
            raise InvalidRequest("Invalid qrId provided")
        if not logEntry:
            raise InvalidRequest("Log entry must be provided")

        # Check if attachment needs to be stored
        storedAttachmentName = None
        if attachmentName is not None and len(attachmentName) > 0:
            attachmentName = Encoder.decode(attachmentName)
            storedAttachmentName = self.storageUtility.storeLogAttachment(cherrypy.request.body, attachmentName)

        logEntry = Encoder.decode(logEntry)

        sessionUser = self.getSessionUser()
        enteredByUserId = sessionUser.get('id')

        logAdded = self.itemControllerImpl.addLogEntryForItemWithQrId(qrId, logEntry, enteredByUserId)

        if storedAttachmentName is not None:
            logId = logAdded.data['id']
            logAttachment = self.logControllerImpl.addLogAttachment(logId, storedAttachmentName, attachmentName, None)
            del(logAttachment.data['log'])
            logAttachmentJsonRep = logAttachment.getFullJsonRep()
            # Refresh log entry
            logAdded.data['logAttachmentAdded'] = logAttachmentJsonRep

        response = logAdded.getFullJsonRep()
        self.logger.debug('Returning log info for item with qrid %s: %s' % (qrId, response))
        return response

