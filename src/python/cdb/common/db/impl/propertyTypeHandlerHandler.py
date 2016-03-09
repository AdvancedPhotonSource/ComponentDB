#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.propertyTypeHandler import PropertyTypeHandler
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler

class PropertyTypeHandlerHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def getPropertyTypeCategories(self, session):
        self.logger.debug('Retrieving property type category list')
        dbPropertyTypeCategories = session.query(PropertyTypeHandler).all()
        return dbPropertyTypeCategories

    def getPropertyTypeHandlerByName(self, session, name):
        try:
            dbPropertyTypeHandler = session.query(PropertyTypeHandler).filter(PropertyTypeHandler.name==name).one()
            return dbPropertyTypeHandler
        except ObjectNotFound, ex:
            raise ObjectNotFound("Property type handler with the name %s could not be found" % (name))

