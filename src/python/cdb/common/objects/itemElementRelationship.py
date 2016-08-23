#!/usr/bin/env python

from cdbObject import CdbObject

class ItemElementRelationship(CdbObject):

    DEFAULT_KEY_LIST = [ 'firstItemElement', 'secondItemElement', 'relationshipType', 'relationshipDetails' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

