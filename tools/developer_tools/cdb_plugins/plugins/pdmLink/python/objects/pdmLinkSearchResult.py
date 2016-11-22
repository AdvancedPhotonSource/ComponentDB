#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.objects.cdbObject import CdbObject

class PdmLinkSearchResult(CdbObject):

    DEFAULT_KEY_LIST = ['number','name']

    def __init__(self, dict):
        CdbObject.__init__(self, dict)
