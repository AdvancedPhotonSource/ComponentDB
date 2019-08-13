#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import itemSource

class ItemSource(CdbDbEntity):

    entityDisplayName = 'item source'

    cdbObjectClass = itemSource.ItemSource

    mappedColumnDict = {
        'part_number' : 'partNumber',
        'is_vendor' : 'isVendor',
        'is_manufacturer' : 'isManufacturer',
        'contact_info' : 'contactInfo'
    }

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


