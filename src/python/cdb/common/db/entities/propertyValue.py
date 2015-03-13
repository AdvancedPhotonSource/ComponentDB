#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import propertyValue

class PropertyValue(CdbDbEntity):

    mappedColumnDict = { 
        'property_type_id' : '',
        'entered_on_date_time' : 'enteredOnDateTime',
        'entered_by_user_id' : 'enteredByUserId',
        'is_dynamic' : 'isDynamic',
        'is_user_writeable' : 'isUserWriteable',
        'display_value' : 'display_value',
        'target_value' : 'target_value',
    }
    cdbObjectClass = propertyValue.propertyValue

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


