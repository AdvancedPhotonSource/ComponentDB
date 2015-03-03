#!/usr/bin/env python

from sqlalchemy.orm import relation
from cdb.common.db.entities.componentTypeCategory import ComponentTypeCategory
from cdb.common.db.entities.componentType import ComponentType
from cdb.common.db.entities.component import Component
from cdb.common.db.entities.userGroup import UserGroup
from cdb.common.db.entities.userInfo import UserInfo
from cdb.common.db.entities.userUserGroup import UserUserGroup

# Map db table/db entity class
CDB_DB_ENTITY_MAP = {
    'component_type_category' : (ComponentTypeCategory, {}),
    'component_type' : (ComponentType, {
        'componentTypeCategory' : relation(ComponentTypeCategory, lazy=False), 
        }),
    'component' : (Component, {
        'componentType' : relation(ComponentType, lazy=False), 
        }),
    'user_info' : (UserInfo, {}), 
    'user_group' : (UserGroup, {}),
    'user_user_group' : (UserUserGroup, {
        'userInfo' : relation(UserInfo, lazy=False), 
        'userGroup' : relation(UserGroup, lazy=False), 
        }),
}

