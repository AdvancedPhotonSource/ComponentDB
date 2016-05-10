#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import itemLog

class ItemLog(CdbDbEntity):

    entityDisplayName = 'item log'

    cdbObjectClass = itemLog.ItemLog

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


