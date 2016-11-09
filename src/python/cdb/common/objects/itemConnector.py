#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdbObject import CdbObject

class ItemConnector(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'label', 'quantity', 'connector' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

