#!/usr/bin/env python

from cdbObject import CdbObject

class ComponentInstanceProperty(CdbObject):

    DEFAULT_KEY_LIST = [ 'componentInstance', 'propertyValue' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

