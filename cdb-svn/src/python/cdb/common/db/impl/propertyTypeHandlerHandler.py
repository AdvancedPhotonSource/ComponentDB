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

    def getPropertyTypeHandlers(self, session):
        return self._getAllDbObjects(session, PropertyTypeHandler)

    def getPropertyTypeHandlerByName(self, session, name):
        return self._findDbObjByName(session, PropertyTypeHandler, name)

    def addPropertyTypeHandler(self, session, propertyTypeHandlerName, description):
        return self._addSimpleNameDescriptionTable(session, PropertyTypeHandler, propertyTypeHandlerName, description)

