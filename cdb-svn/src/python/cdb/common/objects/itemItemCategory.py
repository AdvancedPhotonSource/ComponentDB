#!/usr/bin/env python

from cdbObject import CdbObject

class ItemItemCategory(CdbObject):

    DEFAULT_KEY_LIST = [ 'item', 'category' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

