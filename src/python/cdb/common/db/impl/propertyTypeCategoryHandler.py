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

    def getPropertyTypeCategories(self, session):
        self.logger.debug('Retrieving property type category list')
        dbPropertyTypeCategories = session.query(PropertyTypeCategory).all()
        return dbPropertyTypeCategories

