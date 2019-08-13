#!/usr/bin/env python

from cdbObject import CdbObject

class ComponentProperty(CdbObject):

    DEFAULT_KEY_LIST = [ 'component', 'propertyValue' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

