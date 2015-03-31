#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import designProperty

class DesignProperty(CdbDbEntity):

    mappedColumnDict = {
        'design_id' : 'designId',
        'property_value_id' : 'propertyValueId',
    }
    cdbObjectClass = designProperty.DesignProperty

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


