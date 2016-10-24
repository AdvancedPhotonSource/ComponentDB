#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import relationshipTypeHandler

class RelationshipTypeHandler(CdbDbEntity):

    entityDisplayName = 'relationship type handler'

    cdbObjectClass = relationshipTypeHandler.RelationshipTypeHandler

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)

