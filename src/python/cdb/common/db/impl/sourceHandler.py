#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import datetime
from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.exceptions.multipleObjectsFound import MultipleObjectsFound
from cdb.common.db.entities.source import Source
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler


class SourceHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def findSourceByName(self, session, name):
        try:
            dbSource = session.query(Source).filter(Source.name==name).one()
            return dbSource
        except NoResultFound, ex:
            raise ObjectNotFound('No Source with name %s exists.' % (name))

    def getSources(self, session):
        self.logger.debug('Retrieving source list')
        dbSources = session.query(Source).all()
        return dbSources
    
    def addSource(self, session, sourceName, description, contactInfo, url):
        self.logger.debug('Adding Source %s' % sourceName)
        try:
            dbSource = session.query(Source).filter(Source.name==sourceName).one()
            raise ObjectAlreadyExists('Source %s already exists.' % (sourceName))
        except NoResultFound, ex:
            # ok
            pass

        # Create source Handler
        dbSource = Source(name=sourceName)
        if description:
            dbSource.description = description
        if contactInfo:
            dbSource.contactInfo=contactInfo
        if url:
            dbSource.url = url

        session.add(dbSource)
        session.flush()
        self.logger.debug('Inserted source id %s' % dbSource.id)
        return dbSource

