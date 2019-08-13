#!/usr/bin/env python

from cdbObject import CdbObject

class Attachment(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'name' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

