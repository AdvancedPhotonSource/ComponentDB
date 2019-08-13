#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import item

class Item(CdbDbEntity):

    cdbObjectClass = item.Item

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)

