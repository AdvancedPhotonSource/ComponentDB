#!/usr/bin/env python

from cdbObject import CdbObject

class UserInfo(CdbObject):

    DEFAULT_DISPLAY_KEY_LIST = [ 'id', 'username', 'firstName', 'lastName', 'middleName', 'email', 'description' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

