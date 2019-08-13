#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import itemElementRelationshipHistory

class ItemElementRelationshipHistory(CdbDbEntity):

    entityDisplayName = 'item element relationship history'

    cdbObjectClass = itemElementRelationshipHistory.ItemElementRelationshipHistory

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


