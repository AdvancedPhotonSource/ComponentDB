#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdbObject import CdbObject

class Item(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'name', 'qr_id', 'item_identifier1', 'item_identifier2' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

