#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import componentProperty

class ComponentProperty(CdbDbEntity):

    mappedColumnDict = {
        'component_id' : 'componentId',
        'property_value_id' : 'propertyValueId',
    }
    cdbObjectClass = componentProperty.ComponentProperty

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


