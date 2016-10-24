#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import propertyValue

class PropertyValue(CdbDbEntity):

    mappedColumnDict = { 
        'property_type_id' : 'propertyTypeId',
        'entered_on_date_time' : 'enteredOnDateTime',
        'entered_by_user_id' : 'enteredByUserId',
        'is_dynamic' : 'isDynamic',
        'is_user_writeable' : 'isUserWriteable',
        'display_value' : 'display_value',
        'target_value' : 'target_value',
    }
    cdbObjectClass = propertyValue.PropertyValue

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


