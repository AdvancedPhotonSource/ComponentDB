#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import itemItemCategory

class ItemItemCategory(CdbDbEntity):

    entityDisplayName = 'item item category'

    cdbObjectClass = itemItemCategory.ItemItemCategory

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


