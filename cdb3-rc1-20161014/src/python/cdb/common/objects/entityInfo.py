#!/usr/bin/env python

from cdbObject import CdbObject

class EntityInfo(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'ownerUserInfo', 'ownerUserGroup', 'createdByUserInfo', 'createdOnDateTime', 'lastModifiedByUserInfo', 'lastModifiedOnDateTime', 'isGroupWriteable' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

