#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import resourceTypeCategory

class ResourceTypeCategory(CdbDbEntity):

    entityDisplayName = 'resource type category'

    cdbObjectClass = resourceTypeCategory.ResourceTypeCategory

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)

