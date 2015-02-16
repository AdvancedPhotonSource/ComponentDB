#!/usr/bin/env python

from cdb.common.db.cdbDbEntity import CdbDbEntity

class UserInfo(CdbDbEntity):
    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)

    def getCdbObject(self):
        pass
