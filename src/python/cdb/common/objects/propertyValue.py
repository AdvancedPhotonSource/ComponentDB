#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdbObject import CdbObject

class PropertyValue(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'tag', 'description', 'propertyType', 'value', 'units', 'enteredOnDateTime', 'enteredByUser', 'isUserWriteable', 'isDynamic' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

