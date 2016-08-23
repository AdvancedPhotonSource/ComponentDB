#!/usr/bin/env python

from cdbObject import CdbObject


class ComponentInstanceLocationHistory(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'locationDetails' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)
