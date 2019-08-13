#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import relationshipType

class RelationshipType(CdbDbEntity):

    entityDisplayName = 'relationship type'

    cdbObjectClass = relationshipType.RelationshipType

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)

