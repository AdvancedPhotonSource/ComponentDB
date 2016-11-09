#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import itemElementRelationshipHistory

class ItemElementRelationshipHistory(CdbDbEntity):

    entityDisplayName = 'item element relationship history'

    cdbObjectClass = itemElementRelationshipHistory.ItemElementRelationshipHistory

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


