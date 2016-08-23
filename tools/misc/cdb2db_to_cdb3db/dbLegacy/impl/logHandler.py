#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.exceptions.invalidArgument import InvalidArgument
from dbLegacy.entities.attachment import Attachment
from dbLegacy.entities.logAttachment import LogAttachment
from dbLegacy.entities.log import Log
from dbLegacy.entities.logTopic import LogTopic
from dbLegacy.impl.cdbDbEntityHandler import CdbDbEntityHandler


class LogHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def getLogs(self, session):
        dbLogs = session.query(Log).all()
        return dbLogs

    def getAttachments(self, session):
        dbAttachments = session.query(Attachment).all()
        return dbAttachments

    def getLogAttachments(self, session):
        dbLogAttachments = session.query(LogAttachment).all()
        return dbLogAttachments

    def getLogTopics(self, session):
        dbLogTopics = session.query(LogTopic).all()
        return dbLogTopics