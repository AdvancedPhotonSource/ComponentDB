#!/usr/bin/env python

from cdbObject import CdbObject

class DesignElement(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'name', 'description', 'parentDesign', 'childDesign', 'component', 'location', 'sortOrder', 'entityInfo' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

