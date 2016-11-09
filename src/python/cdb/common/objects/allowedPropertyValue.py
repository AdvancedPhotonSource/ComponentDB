#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdbObject import CdbObject

class AllowedPropertyValue(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'propertyType', 'value', 'units', 'description', 'sortOrder' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

