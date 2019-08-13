#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import itemItemProject

class ItemItemProject(CdbDbEntity):

    entityDisplayName = 'item item project'

    cdbObjectClass = itemItemProject.ItemItemProject

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)

