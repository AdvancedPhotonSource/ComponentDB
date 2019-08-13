#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import propertyType

class PropertyType(CdbDbEntity):

    mappedColumnDict = {
        'property_type_category_id' : 'propertyTypeCategoryId',
        'property_type_handler_id' : 'propertyTypeHandlerId',
        'default_value' : 'defaultValue',
        'default_units' : 'defaultUnits',
    }
    cdbObjectClass = propertyType.PropertyType

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


