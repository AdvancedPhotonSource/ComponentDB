#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import propertyTypeHandler

class PropertyTypeHandler(CdbDbEntity):

    cdbObjectClass = propertyTypeHandler.PropertyTypeHandler

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


