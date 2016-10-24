#!/usr/bin/env python

from cdbObject import CdbObject

class ItemElementProperty(CdbObject):

    DEFAULT_KEY_LIST = [ 'item', 'propertyValue' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

