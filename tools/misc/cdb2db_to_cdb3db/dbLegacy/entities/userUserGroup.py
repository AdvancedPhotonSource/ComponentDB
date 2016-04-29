#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import userUserGroup

class UserUserGroup(CdbDbEntity):

    cdbObjectClass = userUserGroup.UserUserGroup

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)

