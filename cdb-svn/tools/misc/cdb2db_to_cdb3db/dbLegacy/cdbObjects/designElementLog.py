#!/usr/bin/env python

from cdbObject import CdbObject

class DesignElementLog(CdbObject):

    DEFAULT_KEY_LIST = [ 'designElement', 'log' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

