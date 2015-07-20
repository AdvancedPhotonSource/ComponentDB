#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import componentInstance

class ComponentInstance(CdbDbEntity):

    mappedColumnDict = {
        'component_id' : 'componentId',
        'location_id' : 'locationId',
        'serial_number' : 'serialNumber',
        'qr_id' : 'qrId',
        'location_details' : 'locationDetails',
    }
    cdbObjectClass = componentInstance.ComponentInstance

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


