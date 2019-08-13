#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import allowedPropertyValue

class AllowedPropertyValue(CdbDbEntity):

    mappedColumnDict = { 
        'property_type_id' : '',
        'sort_order' : 'sortOrder',
    }
    cdbObjectClass = allowedPropertyValue.AllowedPropertyValue

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


