#!/usr/bin/env python

from cdbObject import CdbObject

class Design(CdbObject):

    DEFAULT_DISPLAY_KEY_LIST = [ 'id', 'name', 'description', 'entityInfo' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

