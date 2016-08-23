#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from datetime import datetime

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.exceptions.invalidArgument import InvalidArgument
from cdb.common.db.entities.attachment import Attachment
from cdb.common.db.entities.logAttachment import LogAttachment
from cdb.common.db.entities.log import Log
from cdb.common.db.entities.logTopic import LogTopic
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler


class LogHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def getLogs(self, session):
        dbLogs = session.query(Log).all()
        return dbLogs

    def findLogById(self, session, id):
        return self._findDbObjById(session, Log, id)

    def getAttachments(self, session):
        dbAttachments = session.query(Attachment).all()
        return dbAttachments

    def getLogAttachments(self, session):
        dbLogAttachments = session.query(LogAttachment).all()
        return dbLogAttachments

    def addLog(self, session, text, enteredByUserId, effectiveFromDateTime, effectiveToDateTime, logTopicName, enteredOnDateTime = None):
        if not enteredOnDateTime:
            enteredOnDateTime = datetime.datetime.now()

        dbLog = Log(text=text)
        dbLog.entered_on_date_time = enteredOnDateTime
        dbLog.entered_by_user_id = enteredByUserId
        if effectiveToDateTime:
            dbLog.effective_from_date_time = effectiveFromDateTime
        if effectiveToDateTime:
            dbLog.effective_to_date_time = effectiveToDateTime

        if logTopicName:
            dbLogTopic = self.findLogTopicByName(session, logTopicName)
            dbLog.logTopic = dbLogTopic

        session.add(dbLog)
        session.flush()

        self.logger.debug('Added Log id %s' % dbLog.id)
        return dbLog

    def addLogTopic(self, session, logTopicName, description):
        return self._addSimpleNameDescriptionTable(session, LogTopic, logTopicName, description)

    def getLogTopics(self, session):
        return self._getAllDbObjects(session, LogTopic)

    def findLogTopicByName(self, session, logTopicName):
        self._findDbObjByName(session, LogTopic, logTopicName)

    def addAttachment(self, session, attachmentName, tag, description):
        self._prepareAddUniqueNameObj(session, Attachment, attachmentName)

        dbAttachment = Attachment(name=attachmentName)
        if description:
            dbAttachment.description = description
        if tag:
            dbAttachment.tag = tag

        session.add(dbAttachment)
        session.flush()

        self.logger.debug('Inserted attachment id %s' % dbAttachment.id)

        return dbAttachment

    def addLogAttachment(self, session, logId, attachmentName, attachmentTag, attachmentDescription):
        dbLog = self.findLogById(session, logId)
        dbAttachment = self.addAttachment(session, attachmentName, attachmentTag, attachmentDescription)

        dbLogAttachment = LogAttachment()
        dbLogAttachment.log = dbLog
        dbLogAttachment.attachment = dbAttachment

        session.add(dbLogAttachment)
        session.flush()

        self.logger.debug('Inserted log attachment for log id %s' % logId)

        return  dbLogAttachment




