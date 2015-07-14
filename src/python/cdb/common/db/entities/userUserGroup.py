#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import userUserGroup

class UserUserGroup(CdbDbEntity):

    cdbObjectClass = userUserGroup.UserUserGroup

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)

