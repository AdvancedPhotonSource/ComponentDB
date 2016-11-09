#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.exceptions.invalidArgument import InvalidArgument
from cdb.common.db.entities.relationshipType import RelationshipType
from cdb.common.db.entities.relationshipTypeHandler import RelationshipTypeHandler as RelationshipTypeHandlerEntity
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler


class RelationshipTypeHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def getRelationshipTypeHandlerByName(self, session, name):
        return self._findDbObjByName(session, RelationshipTypeHandlerEntity, name)

    def getRelationshipTypeByName(self, session, name):
        return self._findDbObjByName(session, RelationshipType, name)

    def addRelationshipTypeHandler(self, session, relationshipTypeHandlerName, description):
        return self._addSimpleNameDescriptionTable(session, RelationshipTypeHandlerEntity, relationshipTypeHandlerName, description)

    def addRelationshipType(self, session, relationshipTypeName, description, relationshipTypeHandlerName):
        self._prepareAddUniqueNameObj(session, RelationshipType, relationshipTypeName)

        # Create RelationshipType Handler
        dbRelationshipType = RelationshipType(name=relationshipTypeName)
        if description:
            dbRelationshipType.description = description
        if relationshipTypeHandlerName:
            dbRelationshipType.relationshipTypeHandler = self.getRelationshipTypeHandlerByName(session, relationshipTypeHandlerName)

        session.add(dbRelationshipType)
        session.flush()
        self.logger.debug('Inserted relationshipType id %s' % dbRelationshipType.id)
        return dbRelationshipType