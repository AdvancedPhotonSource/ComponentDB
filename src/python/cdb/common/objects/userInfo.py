#!/usr/bin/env python

from cdbObject import CdbObject

class UserInfo(CdbObject):

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

