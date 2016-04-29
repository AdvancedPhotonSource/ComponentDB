#!/usr/bin/env python

from cdbObject import CdbObject

class DesignProperty(CdbObject):

    DEFAULT_KEY_LIST = [ 'design', 'propertyValue' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

