#!/usr/bin/env python

from cdbObject import CdbObject

class ComponentLog(CdbObject):

    DEFAULT_KEY_LIST = [ 'component', 'log' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

