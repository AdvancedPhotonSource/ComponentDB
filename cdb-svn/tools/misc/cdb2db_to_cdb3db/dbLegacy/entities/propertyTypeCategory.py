#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import propertyTypeCategory

class PropertyTypeCategory(CdbDbEntity):

    cdbObjectClass = propertyTypeCategory.PropertyTypeCategory

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


