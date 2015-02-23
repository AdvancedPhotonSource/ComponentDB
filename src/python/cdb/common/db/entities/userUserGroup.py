#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity

class UserUserGroup(CdbDbEntity):
    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)

