#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import itemProject

class ItemProject(CdbDbEntity):

    entityDisplayName = 'item project'

    cdbObjectClass = itemProject.ItemProject

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)

