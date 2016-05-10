#!/usr/bin/env python

from cdbObject import CdbObject

class Item(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'name', 'description', 'qr_id', 'item_identifier1', 'item_identifier2' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

