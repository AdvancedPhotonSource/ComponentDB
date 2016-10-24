#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.propertyTypeCategory import PropertyTypeCategory
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler

class PropertyTypeCategoryHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def getPropertyTypeCategoryByName(self, session, name):
        return self._findDbObjByName(session, PropertyTypeCategory, name)

    def getPropertyTypeCategories(self, session):
        return self._getAllDbObjects(session, PropertyTypeCategory)

    def addPropertyTypeCategory(self, session, propertyTypeCategoryName, description):
        return self._addSimpleNameDescriptionTable(session, PropertyTypeCategory, propertyTypeCategoryName, description)
