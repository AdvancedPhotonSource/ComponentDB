#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import componentType

class ComponentType(CdbDbEntity):

    cdbObjectClass = componentType.ComponentType

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


