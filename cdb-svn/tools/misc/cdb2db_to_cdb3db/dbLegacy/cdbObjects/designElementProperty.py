#!/usr/bin/env python

from cdbObject import CdbObject

class DesignElementProperty(CdbObject):

    DEFAULT_KEY_LIST = [ 'designElement', 'propertyValue' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

