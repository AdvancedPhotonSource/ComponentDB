#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.propertyType import PropertyType
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler

class PropertyTypeHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def getPropertyTypes(self, session):
        self.logger.debug('Retrieving property type list')
        dbPropertyTypes = session.query(PropertyType).all()
        return dbPropertyTypes

    def getPropertyTypeById(self, session, id):
        try:
            self.logger.debug('Retrieving property type id %s' % id)
            dbPropertyType = session.query(PropertyType).filter(PropertyType.id==id).one()
            return dbPropertyType
        except NoResultFound, ex:
            raise ObjectNotFound('Property type id %s does not exist.' % (id))


