#!/usr/bin/env python

from cdbObject import CdbObject

class LocationType(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'name', 'description' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

