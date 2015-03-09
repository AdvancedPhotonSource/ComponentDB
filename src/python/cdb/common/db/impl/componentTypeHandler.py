#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.componentType import ComponentType
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler

class ComponentTypeHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def getComponentTypes(self, session):
        self.logger.debug('Retrieving component type list')
        dbComponentTypes = session.query(ComponentType).all()
        return dbComponentTypes

    def getComponentTypeById(self, session, id):
        try:
            self.logger.debug('Retrieving component type id %s' % id)
            dbComponentType = session.query(ComponentType).filter(ComponentType.id==id).one()
            return dbComponentType
        except NoResultFound, ex:
            raise ObjectNotFound('Component type id %s does not exist.' % (id))


