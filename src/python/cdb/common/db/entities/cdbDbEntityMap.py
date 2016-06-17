#!/usr/bin/env python

from cdb.common.db.entities.allowedPropertyValue import AllowedPropertyValue
from cdb.common.db.entities.entityInfo import EntityInfo
from cdb.common.db.entities.propertyTypeCategory import PropertyTypeCategory 
from cdb.common.db.entities.propertyTypeHandler import PropertyTypeHandler
from cdb.common.db.entities.propertyType import PropertyType
from cdb.common.db.entities.propertyValue import PropertyValue
from cdb.common.db.entities.propertyValueHistory import PropertyValueHistory
from cdb.common.db.entities.userGroup import UserGroup
from cdb.common.db.entities.userInfo import UserInfo
from cdb.common.db.entities.userUserGroup import UserUserGroup
from cdb.common.db.entities.attachment import Attachment
from cdb.common.db.entities.logAttachment import LogAttachment
from cdb.common.db.entities.log import Log
from cdb.common.db.entities.entityType import EntityType
from cdb.common.db.entities.domainHandler import DomainHandler
from cdb.common.db.entities.domain import Domain
from cdb.common.db.entities.item import Item
from cdb.common.db.entities.itemElement import ItemElement
from cdb.common.db.entities.itemEntityType import ItemEntityType
from cdb.common.db.entities.source import Source
from cdb.common.db.entities.itemSource import ItemSource
from cdb.common.db.entities.itemCategory import ItemCategory
from cdb.common.db.entities.itemType import ItemType
from cdb.common.db.entities.itemItemType import ItemItemType
from cdb.common.db.entities.itemItemCategory import ItemItemCategory
from cdb.common.db.entities.logTopic import LogTopic
from cdb.common.db.entities.itemElementProperty import ItemElementProperty
from cdb.common.db.entities.relationshipType import RelationshipType
from cdb.common.db.entities.relationshipTypeHandler import RelationshipTypeHandler
from cdb.common.db.entities.itemElementRelationship import ItemElementRelationship
from cdb.common.db.entities.connectorType import ConnectorType
from cdb.common.db.entities.connector import Connector
from cdb.common.db.entities.itemConnector import ItemConnector
from cdb.common.db.entities.resourceTypeCategory import ResourceTypeCategory
from cdb.common.db.entities.resourceType import ResourceType
from cdb.common.db.entities.itemElementLog import ItemElementLog
from cdb.common.db.entities.itemElementRelationshipHistory import ItemElementRelationshipHistory
from cdb.common.db.entities.allowedChildEntityType import AllowedChildEntityType
from cdb.common.db.entities.allowedDomainHandlerEntityType import AllowedDomainHandlerEntityType


# Map db table/db entity class
CDB_DB_ENTITY_MAP = {
    'log_attachment' : (LogAttachment, {
        'attachment' : {'parentEntity' : Attachment, 'lazy' : False },
        'log' : {'parentEntity': Log, 'lazy' : True },
    }),
    'attachment' : (Attachment, {} ),
    'log' : (Log, {
        'logTopic' :  { 'parentEntity' : LogTopic, 'lazy' : False },
    }),
    'log_topic' : (LogTopic, {} ),
    'allowed_property_value' : (AllowedPropertyValue, {
        'propertyType' : { 'parentEntity' : PropertyType, 'lazy' : True }, 
    }),
    'entity_info' : (EntityInfo, {
        'ownerUserInfo' : { 'parentEntity' : UserInfo, 'lazy' : False, 'foreignKeyColumns' : ['owner_user_id'] },
        'ownerUserGroup' : { 'parentEntity' : UserGroup, 'lazy' : False, 'foreignKeyColumns' : ['owner_user_group_id'] }, 
        'createdByUserInfo' : { 'parentEntity' : UserInfo, 'lazy' : False, 'foreignKeyColumns' : ['created_by_user_id'] }, 
        'lastModifiedByUserInfo' : { 'parentEntity' : UserInfo, 'lazy' : False, 'foreignKeyColumns' : ['last_modified_by_user_id'] }, 
    }),
    'property_type_category' : (PropertyTypeCategory, {}),
    'property_type_handler' : (PropertyTypeHandler, {}),
    'property_type' : (PropertyType, {
        'allowedPropertyValueList' : { 'parentEntity' : AllowedPropertyValue, 'lazy' : False },
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
    'allowed_child_entity_type' : (AllowedChildEntityType, {
        'parentEntityType' : { 'parentEntity' : EntityType, 'lazy' : False, 'foreignKeyColumns' : ['parent_entity_type_id'] },
        'childEntityType' : { 'parentEntity' : EntityType, 'lazy' : False, 'foreignKeyColumns' : ['child_entity_type_id'] },
    }),
    'entity_type' : (EntityType, {}),
    'domain_handler' : (DomainHandler, {}),
    'allowed_domain_handler_entity_type' : (AllowedDomainHandlerEntityType, {
        'entityType' : { 'parentEntity' : EntityType, 'lazy' : False },
        'domainHandler' : { 'parentEntity' : DomainHandler, 'lazy' : False },
    }),
    'domain' : (Domain, {
        'domainHandler' : { 'parentEntity' : DomainHandler, 'lazy' : False},
    }),
    'source' : (Source, {}),
    'item_category' : (ItemCategory, {
        'domainHandler' : { 'parentEntity' : DomainHandler, 'lazy' : False},
    }),
    'item_type' : (ItemType, {
        'domainHandler' : { 'parentEntity' : DomainHandler, 'lazy' : False},
    }),
    'item' : (Item, {
        'domain' : { 'parentEntity' : Domain, 'lazy' : False },
        'derivedFromItem' : { 'parentEntity' : Item, 'lazy' : False },
    }),
    'item_source' : ( ItemSource, {
        'item' : { 'parentEntity' : Item, 'lazy' : True },
        'source' : { 'parentEntity' : Source, 'lazy' : False },
    }),
    'item_connector' : ( ItemConnector,{
        'connector' : { 'parentEntity' : Connector, 'lazy' : False },
        'item' : { 'parentEntity' : Item, 'lazy' : True },
    }),
    'item_item_type' : ( ItemItemType, {
        'item' : { 'parentEntity' : Item, 'lazy' : True },
        'type' : { 'parentEntity' : ItemType, 'lazy' : False },
    }),
    'item_item_category' : ( ItemItemCategory, {
        'item' : { 'parentEntity' : Item, 'lazy' : True },
        'category' : { 'parentEntity' : ItemCategory, 'lazy' : False },
    }),
    'item_element' : ( ItemElement, {
        'parentItem' : { 'parentEntity' : Item, 'lazy' : False, 'foreignKeyColumns' : ['parent_item_id'] },
        'containedItem' : { 'parentEntity' : Item, 'lazy' : False, 'foreignKeyColumns' : ['contained_item_id'] },
        'entityInfo' : { 'parentEntity' : EntityInfo, 'lazy' : False },
    }),
    'item_element_log' : ( ItemElementLog, {
       'itemElement' : { 'parentEntity' : ItemElement, 'lazy' : True },
       'log' : { 'parentEntity' : Log, 'lazy' : False },
    }),
    'item_element_property' : ( ItemElementProperty, {
        'itemElement' : { 'parentEntity' : ItemElement, 'lazy' : True },
        'propertyValue' : { 'parentEntity' : PropertyValue, 'lazy' : False },
    }),
    'item_element_relationship' : ( ItemElementRelationship, {
        'firstItemElement' : { 'parentEntity' : ItemElement, 'lazy' : True, 'foreignKeyColumns' : ['first_item_element_id'] },
        'secondItemElement' : { 'parentEntity' : ItemElement, 'lazy' : False, 'foreignKeyColumns' : ['second_item_element_id'] },
        'firstItemConnector' : { 'parentEntity' : ItemConnector, 'lazy' : True, 'foreignKeyColumns' : ['first_item_connector_id'] },
        'secondItemConnector' : { 'parentEntity' : ItemConnector, 'lazy' : True, 'foreignKeyColumns' : ['second_item_connector_id'] },
        'linkItemElement' : { 'parentEntity' : ItemElement, 'lazy' : True, 'foreignKeyColumns' : ['link_item_element_id'] },
        'relationshipType' : { 'parentEntity' : RelationshipType, 'lazy' : False },
        'resourceType' : { 'parentEntity' : ResourceType, 'lazy' : False },
    }),
    'item_element_relationship_history' : ( ItemElementRelationshipHistory, {
        'itemElementRelationship' : { 'parentEntity' : ItemElementRelationship, 'lazy' : True },
        'firstItemElement' : { 'parentEntity' : ItemElement, 'lazy' : True, 'foreignKeyColumns' : ['first_item_element_id'] },
        'secondItemElement' : { 'parentEntity' : ItemElement, 'lazy' : True, 'foreignKeyColumns' : ['second_item_element_id'] },
        'firstItemConnector' : { 'parentEntity' : ItemConnector, 'lazy' : True, 'foreignKeyColumns' : ['first_item_connector_id'] },
        'secondItemConnector' : { 'parentEntity' : ItemConnector, 'lazy' : True, 'foreignKeyColumns' : ['second_item_connector_id'] },
        'linkItemElement' : { 'parentEntity' : ItemElement, 'lazy' : True, 'foreignKeyColumns' : ['link_item_element_id'] },
        'resourceType' : { 'parentEntity' : ResourceType, 'lazy' : True },
        'enteredByUserInfo' : { 'parentEntity' : UserInfo, 'lazy' : True },
    }),
    'item_entity_type' : ( ItemEntityType, {
        'item' : { 'parentEntity' : Item, 'lazy' : False },
        'entityType' : { 'parentEntity' : EntityType, 'lazy' : False }
    }),
    'relationship_type' : ( RelationshipType, {
        'relationshipTypeHandler' : { 'parentEntity' : RelationshipTypeHandler, 'lazy' : False },
    }),
    'relationship_type_handler' : ( RelationshipTypeHandler, {} ),
    'connector_type' : ( ConnectorType, {} ),
    'connector' : ( Connector, {
        'connectorType' : { 'parentEntity' : ConnectorType, 'lazy' : False },
        'resourceType' : { 'parentEntity' : ResourceType, 'lazy' : False }

    } ),
    'resource_type_category' : ( ResourceTypeCategory, {}),
    'resource_type' : ( ResourceType, {
        'resourceTypeCategory' : { 'parentEntity' : ResourceTypeCategory, 'lazy' : False }
    }),


}

