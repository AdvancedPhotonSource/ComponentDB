#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import resourceType

class ResourceType(CdbDbEntity):

    entityDisplayName = 'resource type'

    cdbObjectClass = resourceType.ResourceType

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)

