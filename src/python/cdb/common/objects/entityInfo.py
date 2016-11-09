#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdbObject import CdbObject

class EntityInfo(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'ownerUserInfo', 'ownerUserGroup', 'createdByUserInfo', 'createdOnDateTime', 'lastModifiedByUserInfo', 'lastModifiedOnDateTime', 'isGroupWriteable' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

