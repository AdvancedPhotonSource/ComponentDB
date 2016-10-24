#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import designElementProperty

class DesignElementProperty(CdbDbEntity):

    mappedColumnDict = {
        'design_element_id' : 'designElementId',
        'property_value_id' : 'propertyValueId',
    }
    cdbObjectClass = designElementProperty.DesignElementProperty

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


