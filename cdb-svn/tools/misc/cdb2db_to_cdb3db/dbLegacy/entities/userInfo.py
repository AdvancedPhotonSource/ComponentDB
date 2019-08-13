#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import userInfo

class UserInfo(CdbDbEntity):

    mappedColumnDict = { 
        'first_name' : 'firstName',
        'middle_name' : 'middleName',
        'last_name' : 'lastName',
    }
    cdbObjectClass = userInfo.UserInfo

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)



