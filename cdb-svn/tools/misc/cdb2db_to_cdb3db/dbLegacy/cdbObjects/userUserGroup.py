#!/usr/bin/env python

from cdbObject import CdbObject

class UserUserGroup(CdbObject):

    DEFAULT_KEY_LIST = [ 'user_id', 'user_group_id' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

