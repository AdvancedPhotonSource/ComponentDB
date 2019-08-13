#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import itemCategory

class ItemCategory(CdbDbEntity):

    entityDisplayName = 'item category'

    cdbObjectClass = itemCategory.ItemCategory

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)

