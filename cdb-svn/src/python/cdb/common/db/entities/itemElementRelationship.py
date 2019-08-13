#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import itemElementRelationship

class ItemElementRelationship(CdbDbEntity):

    entityDisplayName = 'item element relationship'

    cdbObjectClass = itemElementRelationship.ItemElementRelationship

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


