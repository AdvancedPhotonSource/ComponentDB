#!/usr/bin/env python

from cdbObject import CdbObject

class PropertyType(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'name', 'description', 'propertyTypeCategory', 'propertyTypeHandler', 'defaultValue', 'defaultUnits' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

