#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import allowedPropertyValue

class AllowedPropertyValue(CdbDbEntity):

    entityDisplayName = 'allowed property value'

    mappedColumnDict = { 
        'property_type_id' : '',
        'sort_order' : 'sortOrder',
    }
    cdbObjectClass = allowedPropertyValue.AllowedPropertyValue

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


