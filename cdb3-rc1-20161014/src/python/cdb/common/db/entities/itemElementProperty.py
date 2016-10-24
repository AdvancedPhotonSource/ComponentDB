#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import itemElementProperty

class ItemElementProperty(CdbDbEntity):

    entityDisplayName = 'item element property'

    cdbObjectClass = itemElementProperty.ItemElementProperty

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


