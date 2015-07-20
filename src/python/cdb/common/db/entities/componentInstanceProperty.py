#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import componentInstanceProperty

class ComponentInstanceProperty(CdbDbEntity):

    mappedColumnDict = {
        'component_instance_id' : 'componentInstanceId',
        'property_value_id' : 'propertyValueId',
    }
    cdbObjectClass = componentInstanceProperty.ComponentInstanceProperty

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


