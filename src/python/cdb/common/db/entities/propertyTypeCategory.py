#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import propertyTypeCategory

class PropertyTypeCategory(CdbDbEntity):

    cdbObjectClass = propertyTypeCategory.PropertyTypeCategory

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


