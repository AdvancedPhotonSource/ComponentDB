#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.componentType import ComponentType
from cdb.common.db.entities.component import Component
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler

class ComponentHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def getComponentList(self, session):
        self.logger.debug('Retrieving component list')
        dbComponentList = session.query(Component).all()
        return dbComponentList

