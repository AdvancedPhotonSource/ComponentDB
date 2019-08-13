#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import propertyTypeHandler

class PropertyTypeHandler(CdbDbEntity):

    cdbObjectClass = propertyTypeHandler.PropertyTypeHandler

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


