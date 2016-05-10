#!/usr/bin/env python

from cdbObject import CdbObject

class ItemLog(CdbObject):

    DEFAULT_KEY_LIST = [ 'item', 'log' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

