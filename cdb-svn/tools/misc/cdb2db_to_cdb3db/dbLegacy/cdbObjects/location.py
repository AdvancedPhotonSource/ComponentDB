#!/usr/bin/env python

from cdbObject import CdbObject

class Location(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'name', 'description', 'locationType', 'isUserWriteable', 'sortOrder' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

