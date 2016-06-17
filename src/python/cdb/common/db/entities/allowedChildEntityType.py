#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import allowedChildEntityType

class AllowedChildEntityType(CdbDbEntity):

    entityDisplayName = 'allowed child entity type'

    cdbObjectClass = allowedChildEntityType.AllowedChildEntityType

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


