#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import userGroup

class UserGroup(CdbDbEntity):

    cdbObjectClass = userGroup.UserGroup

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


