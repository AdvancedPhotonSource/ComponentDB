#!/usr/bin/env python

from cdbObject import CdbObject

class PropertyValueHistory(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'tag', 'description', 'propertyValue', 'value', 'units', 'enteredOnDateTime', 'enteredByUser' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

