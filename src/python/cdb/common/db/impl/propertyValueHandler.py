#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.propertyValue import PropertyValue
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler

class PropertyValueHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def getPropertyValues(self, session):
        self.logger.debug('Retrieving property value list')
        dbPropertyValues = session.query(PropertyValue).all()
        return dbPropertyValues

    def getPropertyValueById(self, session, id):
        try:
            self.logger.debug('Retrieving property value id %s' % id)
            dbPropertyValue = session.query(PropertyValue).filter(PropertyValue.id==id).one()
            return dbPropertyValue
        except NoResultFound, ex:
            raise ObjectNotFound('Property value id %s does not exist.' % (id))


