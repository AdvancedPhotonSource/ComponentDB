#!/usr/bin/env python

from cdbObject import CdbObject

class DesignLog(CdbObject):

    DEFAULT_KEY_LIST = [ 'design', 'log' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

