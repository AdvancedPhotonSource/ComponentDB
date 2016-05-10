#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.exceptions.invalidArgument import InvalidArgument
from cdb.common.db.entities.resourceType import ResourceType
from cdb.common.db.entities.resourceTypeCategory import ResourceTypeCategory
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler


class ResourceTypeHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def getResourceTypeByName(self, session, resourceTypeName):
        return self._findDbObjByName(session, ResourceType, resourceTypeName)

    def getResourceTypeCategoryByName(self, session, resourceTypeCategoryName):
        return self._findDbObjByName(session, ResourceTypeCategory, resourceTypeCategoryName)