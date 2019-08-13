#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import itemEntityType

class ItemEntityType(CdbDbEntity):

    entityDisplayName = 'item entity type'

    cdbObjectClass = itemEntityType.ItemEntityType

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


