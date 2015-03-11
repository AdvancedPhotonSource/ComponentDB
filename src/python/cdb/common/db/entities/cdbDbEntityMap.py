#!/usr/bin/env python

from cdb.common.db.entities.componentTypeCategory import ComponentTypeCategory
from cdb.common.db.entities.componentType import ComponentType
from cdb.common.db.entities.component import Component
from cdb.common.db.entities.design import Design
from cdb.common.db.entities.entityInfo import EntityInfo
from cdb.common.db.entities.userGroup import UserGroup
from cdb.common.db.entities.userInfo import UserInfo
from cdb.common.db.entities.userUserGroup import UserUserGroup

# Map db table/db entity class
CDB_DB_ENTITY_MAP = {
    'component_type_category' : (ComponentTypeCategory, {}),
    'component_type' : (ComponentType, {
        'componentTypeCategory' : { 'parentEntity' : ComponentTypeCategory, 'lazy' : False }, 
        }),
    'component' : (Component, {
        'componentType' : { 'parentEntity' : ComponentType, 'lazy' : False }, 
        'entityInfo' : { 'parentEntity' : EntityInfo, 'lazy' : False }, 
        }),
    'design' : (Design, {
        'entityInfo' : { 'parentEntity' : EntityInfo, 'lazy' : False }, 
        }),
    'entity_info' : (EntityInfo, {
        'ownerUserInfo' : { 'parentEntity' : UserInfo, 'lazy' : False, 'foreignKeyColumns' : ['owner_user_id'] }, 
        'ownerUserGroup' : { 'parentEntity' : UserGroup, 'lazy' : False, 'foreignKeyColumns' : ['owner_user_group_id'] }, 
        'createdByUserInfo' : { 'parentEntity' : UserInfo, 'lazy' : False, 'foreignKeyColumns' : ['created_by_user_id'] }, 
        'lastModifiedByUserInfo' : { 'parentEntity' : UserInfo, 'lazy' : False, 'foreignKeyColumns' : ['last_modified_by_user_id'] }, 
        }), 
    'user_info' : (UserInfo, {}), 
    'user_group' : (UserGroup, {}),
    'user_user_group' : (UserUserGroup, {
        'userInfo' : { 'parentEntity' : UserInfo, 'lazy' : False }, 
        'userGroup' : { 'parentEntity' : UserGroup, 'lazy' : False }, 
        }),
}

