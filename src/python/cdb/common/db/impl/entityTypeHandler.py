#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.exceptions.invalidArgument import InvalidArgument
from cdb.common.db.entities.entityType import EntityType
from cdb.common.db.entities.allowedChildEntityType import AllowedChildEntityType
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler


class EntityTypeHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def findEntityTypeById(self, session, id):
        return self._findDbObjById(session, EntityType, id)

    def findEntityTypeByName(self, session, name):
        return self._findDbObjByName(session, EntityType, name)

    def addEntityType(self, session, entityTypeName, description):
        return self._addSimpleNameDescriptionTable(session, EntityType, entityTypeName, description)

    def addAllowedChildEntityType(self, session, parentEntityTypeName, childEntityTypeName):
        parentEntityType = self.findEntityTypeByName(session, parentEntityTypeName)
        childEntityType = self.findEntityTypeByName(session, childEntityTypeName)

        dbAllowedChildEntityType = AllowedChildEntityType()

        dbAllowedChildEntityType.parentEntityType = parentEntityType
        dbAllowedChildEntityType.childEntityType = childEntityType

        session.add(dbAllowedChildEntityType)
        session.flush()
        self.logger.debug('Inserted allowed child entity type %s for parent %s' % (childEntityTypeName, parentEntityTypeName))

        return dbAllowedChildEntityType


