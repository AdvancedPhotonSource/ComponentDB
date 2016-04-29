#!/usr/bin/env python

from dbLegacy.entities.allowedPropertyValue import AllowedPropertyValue
from dbLegacy.entities.componentTypeCategory import ComponentTypeCategory
from dbLegacy.entities.componentType import ComponentType
from dbLegacy.entities.component import Component
from dbLegacy.entities.componentProperty import ComponentProperty
from dbLegacy.entities.componentInstance import ComponentInstance
from dbLegacy.entities.componentInstanceProperty import ComponentInstanceProperty
from dbLegacy.entities.componentInstanceLocationHistory import ComponentInstanceLocationHistory
from dbLegacy.entities.design import Design
from dbLegacy.entities.designProperty import DesignProperty
from dbLegacy.entities.designElement import DesignElement
from dbLegacy.entities.designElementProperty import DesignElementProperty
from dbLegacy.entities.entityInfo import EntityInfo
from dbLegacy.entities.locationType import LocationType
from dbLegacy.entities.location import Location
from dbLegacy.entities.propertyTypeCategory import PropertyTypeCategory
from dbLegacy.entities.propertyTypeHandler import PropertyTypeHandler
from dbLegacy.entities.propertyType import PropertyType
from dbLegacy.entities.propertyValue import PropertyValue
from dbLegacy.entities.propertyValueHistory import PropertyValueHistory
from dbLegacy.entities.userGroup import UserGroup
from dbLegacy.entities.userInfo import UserInfo
from dbLegacy.entities.userUserGroup import UserUserGroup
from dbLegacy.entities.attachment import Attachment
from dbLegacy.entities.logAttachment import LogAttachment
from dbLegacy.entities.log import Log

# Map db table/db entity class
CDB_DB_ENTITY_MAP = {
    'log_attachment' : (LogAttachment, {
        'attachment' : {'parentEntity' : Attachment, 'lazy' : False },
        'log' : {'parentEntity': Log, 'lazy' : False },
    }),
    'attachment' : (Attachment, {} ),
    'log' : (Log, {} ),
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
        'componentInstance' : { 'parentEntity' : ComponentInstance, 'lazy' : False},
        'componentProperty' : { 'parentEntity' : ComponentProperty, 'lazy' : False },
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
    'component_instance_location_history' : (ComponentInstanceLocationHistory, {
        'componentInstance' : {'parentEntity' : ComponentInstance, 'lazy' : True},
        'userInfo' : {'parentEntity' : UserInfo, 'lazy' : False},
        'location' : {'parentEntity' : Location, 'lazy' : False}
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
        'propertyValueHistory' : { 'parentEntity' : PropertyValueHistory, 'lazy' : False },
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

