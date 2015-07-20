#!/usr/bin/env python

from cdb.common.db.entities.allowedPropertyValue import AllowedPropertyValue
from cdb.common.db.entities.componentTypeCategory import ComponentTypeCategory
from cdb.common.db.entities.componentType import ComponentType
from cdb.common.db.entities.component import Component
from cdb.common.db.entities.componentProperty import ComponentProperty
from cdb.common.db.entities.componentInstance import ComponentInstance
from cdb.common.db.entities.componentInstanceProperty import ComponentInstanceProperty
from cdb.common.db.entities.design import Design
from cdb.common.db.entities.designProperty import DesignProperty
from cdb.common.db.entities.designElement import DesignElement
from cdb.common.db.entities.designElementProperty import DesignElementProperty
from cdb.common.db.entities.entityInfo import EntityInfo
from cdb.common.db.entities.locationType import LocationType
from cdb.common.db.entities.location import Location
from cdb.common.db.entities.propertyTypeCategory import PropertyTypeCategory 
from cdb.common.db.entities.propertyTypeHandler import PropertyTypeHandler
from cdb.common.db.entities.propertyType import PropertyType
from cdb.common.db.entities.propertyValue import PropertyValue
from cdb.common.db.entities.propertyValueHistory import PropertyValueHistory
from cdb.common.db.entities.userGroup import UserGroup
from cdb.common.db.entities.userInfo import UserInfo
from cdb.common.db.entities.userUserGroup import UserUserGroup

# Map db table/db entity class
CDB_DB_ENTITY_MAP = {
    'allowed_property_value' : (AllowedPropertyValue, {
        'propertyType' : { 'parentEntity' : PropertyType, 'lazy' : True }, 
    }),
    'component_type_category' : (ComponentTypeCategory, {}),
    'component_type' : (ComponentType, {
        'componentTypeCategory' : { 'parentEntity' : ComponentTypeCategory, 'lazy' : False }, 
    }),
    'component' : (Component, {
        'componentType' : { 'parentEntity' : ComponentType, 'lazy' : False }, 
        'entityInfo' : { 'parentEntity' : EntityInfo, 'lazy' : False }, 
    }),
    'component_instance' : (ComponentInstance, {
        'component' : { 'parentEntity' : Component, 'lazy' : False }, 
        'location' : { 'parentEntity' : Location, 'lazy' : False }, 
        'entityInfo' : { 'parentEntity' : EntityInfo, 'lazy' : False }, 
    }),
    'component_instance_property' : (ComponentInstanceProperty, {
        'componentInstance' : { 'parentEntity' : ComponentInstance, 'lazy' : True}, 
        'propertyValue' : { 'parentEntity' : PropertyValue, 'lazy' : False }, 
    }),
    'component_property' : (ComponentProperty, {
        'component' : { 'parentEntity' : Component, 'lazy' : True }, 
        'propertyValue' : { 'parentEntity' : PropertyValue, 'lazy' : False }, 
    }),
    'design' : (Design, {
        'entityInfo' : { 'parentEntity' : EntityInfo, 'lazy' : False }, 
    }),
    'design_property' : (DesignProperty, {
        'design' : { 'parentEntity' : Design, 'lazy' : True }, 
        'propertyValue' : { 'parentEntity' : PropertyValue, 'lazy' : False }, 
    }),
    'design_element' : (DesignElement, {
        'parentDesign' : { 'parentEntity' : Design, 'lazy' : True, 'foreignKeyColumns' : ['parent_design_id'] }, 
        'childDesign' : { 'parentEntity' : Design, 'lazy' : False, 'foreignKeyColumns' : ['child_design_id'] }, 
        'component' : { 'parentEntity' : Component, 'lazy' : False }, 
        'location' : { 'parentEntity' : Location, 'lazy' : False }, 
        'entityInfo' : { 'parentEntity' : EntityInfo, 'lazy' : False }, 
    }),
    'design_element_property' : (DesignElementProperty, {
        'designElement' : { 'parentEntity' : DesignElement, 'lazy' : False }, 
        'propertyValue' : { 'parentEntity' : PropertyValue, 'lazy' : False }, 
    }),
    'entity_info' : (EntityInfo, {
        'ownerUserInfo' : { 'parentEntity' : UserInfo, 'lazy' : False, 'foreignKeyColumns' : ['owner_user_id'] }, 
        'ownerUserGroup' : { 'parentEntity' : UserGroup, 'lazy' : False, 'foreignKeyColumns' : ['owner_user_group_id'] }, 
        'createdByUserInfo' : { 'parentEntity' : UserInfo, 'lazy' : False, 'foreignKeyColumns' : ['created_by_user_id'] }, 
        'lastModifiedByUserInfo' : { 'parentEntity' : UserInfo, 'lazy' : False, 'foreignKeyColumns' : ['last_modified_by_user_id'] }, 
    }), 
    'location_type' : (LocationType, {}),
    'location' : (Location, {
        'locationType' : { 'parentEntity' : LocationType, 'lazy' : False }, 
    }),
    'property_type_category' : (PropertyTypeCategory, {}),
    'property_type_handler' : (PropertyTypeHandler, {}),
    'property_type' : (PropertyType, {
        'propertyTypeCategory' : { 'parentEntity' : PropertyTypeCategory, 'lazy' : False }, 
        'propertyTypeHandler' : { 'parentEntity' : PropertyTypeHandler, 'lazy' : False }, 
    }),
    'property_value' : (PropertyValue, {
        'propertyType' : { 'parentEntity' : PropertyType, 'lazy' : False }, 
        'enteredByUserInfo' : { 'parentEntity' : UserInfo, 'lazy' : False }, 
    }),
    'property_value_history' : (PropertyValueHistory, {
        'propertyValue' : { 'parentEntity' : PropertyValue, 'lazy' : False }, 
    }),
    'user_info' : (UserInfo, {}), 
    'user_group' : (UserGroup, {}),
    'user_user_group' : (UserUserGroup, {
        'userInfo' : { 'parentEntity' : UserInfo, 'lazy' : False }, 
        'userGroup' : { 'parentEntity' : UserGroup, 'lazy' : False }, 
    }),
}

