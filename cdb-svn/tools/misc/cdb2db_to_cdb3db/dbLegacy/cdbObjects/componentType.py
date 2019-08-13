#!/usr/bin/env python

from cdbObject import CdbObject

class ComponentType(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'name', 'description', 'componentTypeCategory' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

