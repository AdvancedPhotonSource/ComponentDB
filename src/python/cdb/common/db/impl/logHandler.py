#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.exceptions.invalidArgument import InvalidArgument
from cdb.common.db.entities.attachment import Attachment
from cdb.common.db.entities.logAttachment import LogAttachment
from cdb.common.db.entities.log import Log
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler


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

