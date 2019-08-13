#!/usr/bin/env python

from cdbObject import CdbObject

class ItemItemProject(CdbObject):

    DEFAULT_KEY_LIST = [ 'item', 'project' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

