#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.componentTypeCategory import ComponentTypeCategory
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler

class ComponentTypeCategoryHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def getComponentTypeCategories(self, session):
        self.logger.debug('Retrieving component type category list')
        dbComponentTypeCategories = session.query(ComponentTypeCategory).all()
        return dbComponentTypeCategories

