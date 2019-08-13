#!/usr/bin/env python

from cdbObject import CdbObject

class ItemSource(CdbObject):

    DEFAULT_KEY_LIST = [ 'item', 'source' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

