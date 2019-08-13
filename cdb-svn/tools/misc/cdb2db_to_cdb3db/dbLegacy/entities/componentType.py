#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import componentType

class ComponentType(CdbDbEntity):

    cdbObjectClass = componentType.ComponentType

    mappedColumnDict = {
        'component_type_category_id' : 'componentTypeCategoryId',
    }

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


