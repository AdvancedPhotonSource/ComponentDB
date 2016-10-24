#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import componentInstanceProperty

class ComponentInstanceProperty(CdbDbEntity):

    mappedColumnDict = {
        'component_instance_id' : 'componentInstanceId',
        'property_value_id' : 'propertyValueId',
    }
    cdbObjectClass = componentInstanceProperty.ComponentInstanceProperty

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


