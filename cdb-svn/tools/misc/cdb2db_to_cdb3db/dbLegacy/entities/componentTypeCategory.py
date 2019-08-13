#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import componentTypeCategory

class ComponentTypeCategory(CdbDbEntity):

    cdbObjectClass = componentTypeCategory.ComponentTypeCategory

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


