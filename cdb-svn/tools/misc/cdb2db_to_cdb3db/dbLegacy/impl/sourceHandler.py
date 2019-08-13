#!/usr/bin/env python

import datetime
from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.exceptions.multipleObjectsFound import MultipleObjectsFound
from dbLegacy.entities.source import Source
from dbLegacy.impl.cdbDbEntityHandler import CdbDbEntityHandler


class SourceHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def getSources(self, session):
        self.logger.debug('Retrieving source list')
        dbSources = session.query(Source).all()
        return dbSources


