#!/usr/bin/env python

from cdbObject import CdbObject

class PropertyMetadata(CdbObject):

    DEFAULT_KEY_LIST = [ 'metadataKey', 'metadataValue']

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

