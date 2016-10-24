#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import itemElement

class ItemElement(CdbDbEntity):

    entityDisplayName = 'item element'

    cdbObjectClass = itemElement.ItemElement

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


