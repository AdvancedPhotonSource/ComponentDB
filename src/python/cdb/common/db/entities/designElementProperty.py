#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import designElementProperty

class DesignElementProperty(CdbDbEntity):

    mappedColumnDict = {
        'design_element_id' : 'designElementId',
        'property_value_id' : 'propertyValueId',
    }
    cdbObjectClass = designElementProperty.DesignElementProperty

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


