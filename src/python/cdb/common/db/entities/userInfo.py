#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import userInfo

class UserInfo(CdbDbEntity):

    mappedColumnDict = { 
        'first_name' : 'firstName',
        'middle_name' : 'middleName',
        'last_name' : 'lastName',
    }
    cdbObjectClass = userInfo.UserInfo

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)



