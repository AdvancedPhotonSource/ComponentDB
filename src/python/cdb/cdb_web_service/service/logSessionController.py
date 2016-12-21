#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import cherrypy
from cdb.cdb_web_service.impl.logControllerImpl import LogControllerImpl
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.service.cdbSessionController import CdbSessionController
from cdb.common.utility.encoder import Encoder


class LogSessionController(CdbSessionController):
    def __init__(self):
        CdbSessionController.__init__(self)
        self.logControllerImpl = LogControllerImpl()

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def addLogAttachment(self, logId, attachmentName, attachmentDescription=None, **kwargs):
        if not logId:
            raise InvalidRequest("Invalid logId provided")
        if not attachmentName:
            raise InvalidRequest("Invalid attachment name provided")

        attachmentName = Encoder.decode(attachmentName)
        attachmentDescription = Encoder.decode(attachmentDescription)
        cherrypyData = cherrypy.request.body
        sessionUser = self.getSessionUser()
        attachmentAddedByUserId = sessionUser.get('id')

        logAttachmentAdded = self.logControllerImpl.addLogAttachment(logId, attachmentName, attachmentDescription
                                                                     , attachmentAddedByUserId, cherrypyData)

        response = logAttachmentAdded.getFullJsonRep()
        self.logger.debug('Returning log attachment info for log with id %s: %s' % (logId, response))
        return response

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def updateLogEntry(self, logId, text=None, effectiveFromDateTime=None, effectiveToDateTime=None,
                       logTopicName=None):
        sessionUser = self.getSessionUser()
        userId = sessionUser.get('id')

        if text is not None:
            text = Encoder.decode(text)

        if effectiveFromDateTime is not None:
            effectiveFromDateTime = Encoder.decode(effectiveFromDateTime)

        if effectiveToDateTime is not None:
            effectiveToDateTime = Encoder.decode(effectiveToDateTime)

        if logTopicName is not None:
            logTopicName = Encoder.decode(logTopicName)

        logObject = self.logControllerImpl.updateLogEntry(logId, userId, text, effectiveFromDateTime, effectiveToDateTime, logTopicName)

        response = logObject.getFullJsonRep()
        self.logger.debug('Return updated log entry for log with id %s' % logId)
        return response

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def deleteLogEntry(self, logId):
        sessionUser = self.getSessionUser()
        userId = sessionUser.get('id')

        self.logControllerImpl.deleteLogEntry(logId, userId)

        return {}