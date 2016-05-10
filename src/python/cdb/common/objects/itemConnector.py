#!/usr/bin/env python

from cdbObject import CdbObject

class ItemConnector(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'label', 'quantity', 'connector' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

