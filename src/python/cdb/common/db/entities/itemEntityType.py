#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import itemEntityType

class ItemEntityType(CdbDbEntity):

    entityDisplayName = 'item entity type'

    cdbObjectClass = itemEntityType.ItemEntityType

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


