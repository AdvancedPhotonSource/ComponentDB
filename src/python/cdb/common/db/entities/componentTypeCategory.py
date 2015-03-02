#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import componentTypeCategory

class ComponentTypeCategory(CdbDbEntity):

    cdbObjectClass = componentTypeCategory.ComponentTypeCategory

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


