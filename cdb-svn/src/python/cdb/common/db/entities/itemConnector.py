#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import itemConnector

class ItemConnector(CdbDbEntity):

    entityDisplayName = 'item connector'

    cdbObjectClass = itemConnector.ItemConnector

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)

