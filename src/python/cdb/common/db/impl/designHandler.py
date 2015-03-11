#!/usr/bin/env python

import datetime
from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.design import Design
from cdb.common.db.entities.entityInfo import EntityInfo
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler
from entityInfoHandler import EntityInfoHandler

class DesignHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.entityInfoHandler = EntityInfoHandler()

    def getDesigns(self, session):
        self.logger.debug('Retrieving design list')
        dbDesigns = session.query(Design).all()
        return dbDesigns

    def getDesignById(self, session, id):
        try:
            self.logger.debug('Retrieving design id %s' % id)
            dbDesign = session.query(Design).filter(Design.id==id).one()
            return dbDesign
        except NoResultFound, ex:
            raise ObjectNotFound('Design id %s does not exist.' % (id))

    def getDesignByName(self, session, name):
        try:
            self.logger.debug('Retrieving design name %s' % name)
            dbDesign = session.query(Design).filter(Design.name==name).one()
            return dbDesign
        except NoResultFound, ex:
            raise ObjectNotFound('Design with name %s does not exist.' % (name))

    def addDesign(self, session, name, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description=None):
        try:
            dbDesign = session.query(Design).filter(Design.name==name).one()
            raise ObjectAlreadyExists('Design %s already exists.' % name)
        except NoResultFound, ex:
            # OK.
            pass

        # Create entity info
        dbEntityInfo = self.entityInfoHandler.createEntityInfo(session, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable)

        # Create design
        dbDesign = Design(name=name, description=description)
        dbDesign.entityInfo = dbEntityInfo
        session.add(dbDesign)
        session.flush()
        self.logger.debug('Inserted design id %s' % dbDesign.id)
        return dbDesign

