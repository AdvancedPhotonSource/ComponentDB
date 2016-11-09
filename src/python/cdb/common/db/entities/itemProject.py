#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import itemProject

class ItemProject(CdbDbEntity):

    entityDisplayName = 'item project'

    cdbObjectClass = itemProject.ItemProject

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)

