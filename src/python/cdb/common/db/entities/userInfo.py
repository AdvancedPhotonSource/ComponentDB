#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import userInfo

class UserInfo(CdbDbEntity):

    entityDisplayName = 'user info'

    mappedColumnDict = { 
        'first_name' : 'firstName',
        'middle_name' : 'middleName',
        'last_name' : 'lastName',
    }
    cdbObjectClass = userInfo.UserInfo

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)



