#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdbObject import CdbObject

class ItemSource(CdbObject):

    DEFAULT_KEY_LIST = [ 'item', 'source' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

