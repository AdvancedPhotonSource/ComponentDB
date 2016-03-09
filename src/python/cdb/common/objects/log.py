#!/usr/bin/env python

from cdbObject import CdbObject

class Log(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'text' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

