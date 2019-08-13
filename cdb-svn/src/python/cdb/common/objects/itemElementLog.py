#!/usr/bin/env python

from cdbObject import CdbObject

class ItemElementLog(CdbObject):

    DEFAULT_KEY_LIST = [ 'itemElement', 'log' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

