#!/usr/bin/env python

from cdbObject import CdbObject

class AllowedPropertyValue(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'propertyType', 'value', 'units', 'description', 'sortOrder' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

