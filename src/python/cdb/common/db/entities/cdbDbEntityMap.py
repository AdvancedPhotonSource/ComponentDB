#!/usr/bin/env python

from sqlalchemy.orm import relation
from cdb.common.db.entities.userGroup import UserGroup
from cdb.common.db.entities.userInfo import UserInfo
from cdb.common.db.entities.userUserGroup import UserUserGroup

CDB_DB_ENTITY_MAP = {
    'user_info' : (UserInfo, {}), 
    'user_group' : (UserGroup, {}),
    'user_user_group' : (UserUserGroup, {
        'userInfo' : relation(UserInfo, lazy=False), 
        'userGroup' : relation(UserGroup, lazy=False), }),
}

