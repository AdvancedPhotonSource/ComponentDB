#!/usr/bin/env python

from cdbObject import CdbObject

class ComponentInstance(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'component', 'location', 'tag', 'serialNumber', 'qrId', 'locationDetails', 'description', 'entityInfo' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

