#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import itemItemType

class ItemItemType(CdbDbEntity):

    entityDisplayName = 'item item type'

    cdbObjectClass = itemItemType.ItemItemType

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


