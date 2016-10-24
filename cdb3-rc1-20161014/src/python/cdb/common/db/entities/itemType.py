#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import itemType

class ItemType(CdbDbEntity):

    entityDisplayName = 'item type'

    cdbObjectClass = itemType.ItemType

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)

