#!/usr/bin/env python

from cdbObject import CdbObject

class ItemElementRelationshipHistory(CdbObject):

    DEFAULT_KEY_LIST = [ 'firstItemElement', 'secondItemElement', 'relationshipDetails' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

