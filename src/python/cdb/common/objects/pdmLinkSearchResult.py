#!/usr/bin/env python

from cdbObject import CdbObject

class PdmLinkSearchResult(CdbObject):

    DEFAULT_KEY_LIST = ['name']

    def __init__(self, dict):
        CdbObject.__init__(self, dict)
