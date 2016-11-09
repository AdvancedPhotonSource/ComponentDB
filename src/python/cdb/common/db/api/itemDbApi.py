#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.db.api.cdbDbApi import CdbDbApi
from cdb.common.db.impl.entityTypeHandler import EntityTypeHandler
from cdb.common.db.impl.domainHandler import DomainHandler
from cdb.common.db.impl.itemHandler import ItemHandler
from cdb.common.db.impl.sourceHandler import SourceHandler
from cdb.common.db.impl.relationshipTypeHandler import RelationshipTypeHandler

class ItemDbApi(CdbDbApi):

    def __init__(self):
        CdbDbApi.__init__(self)
        self.entityTypeHandler = EntityTypeHandler()
        self.domainHandler = DomainHandler()
        self.itemHandler = ItemHandler()
        self.sourceHandler = SourceHandler()
        self.relationshipTypeHandler = RelationshipTypeHandler()

    @CdbDbApi.executeTransaction
    def addEntityType(self, name, description, **kwargs):
        session = kwargs['session']
        dbEntityType = self.entityTypeHandler.addEntityType(session, name, description)
        return dbEntityType.getCdbObject()

    @CdbDbApi.executeQuery
    def getEntityTypeByName(self, name, **kwargs):
        session = kwargs['session']
        dbEntityType = self.entityTypeHandler.findEntityTypeByName(session, name)
        return dbEntityType.getCdbObject()

    @CdbDbApi.executeTransaction
    def addAllowedChildEntityType(self, parentEntityTypeName, childEntityTypeName, **kwargs):
        session = kwargs['session']
        dbEntityType = self.entityTypeHandler.addAllowedChildEntityType(session, parentEntityTypeName, childEntityTypeName)
        return dbEntityType.getCdbObject()

    @CdbDbApi.executeQuery
    def getDomainByName(self, name, **kwargs):
        session = kwargs['session']
        dbDomain = self.domainHandler.findDomainByName(session, name)
        return dbDomain.getCdbObject()

    @CdbDbApi.executeTransaction
    def addAllowedEntityTypeDomain(self, domainName, entityTypeName, **kwargs):
        session = kwargs['session']
        dbAllowedEntityTypeDomain = self.domainHandler.addAllowedEntityType(session, domainName, entityTypeName)
        return dbAllowedEntityTypeDomain.getCdbObject()

    @CdbDbApi.executeTransaction
    def addDomain(self, name, description, **kwargs):
        session = kwargs['session']
        dbDomain = self.domainHandler.addDomain(session, name, description)
        return dbDomain.getCdbObject()

    @CdbDbApi.executeTransaction
    def addRelationshipTypeHandler(self, name, description, **kwargs):
        session = kwargs['session']
        dbRelationshipTypeHandler = self.relationshipTypeHandler.addRelationshipTypeHandler(session, name, description)
        return dbRelationshipTypeHandler.getCdbObject()

    @CdbDbApi.executeQuery
    def getRelationshipTypeHandlerByName(self, name, **kwargs):
        session = kwargs['session']
        dbRelationshipTypeHandler = self.relationshipTypeHandler.getRelationshipTypeHandlerByName(session, name)
        return dbRelationshipTypeHandler.getCdbObject()

    @CdbDbApi.executeTransaction
    def addRelationshipType(self, name, description, relationshipTypeHandlerName, **kwargs):
        session = kwargs['session']
        dbRelationshipType = self.relationshipTypeHandler.addRelationshipType(session, name, description, relationshipTypeHandlerName)
        return dbRelationshipType.getCdbObject()

    @CdbDbApi.executeQuery
    def getRelationshipTypeByName(self, name, **kwargs):
        session = kwargs['session']
        dbRelationshipType = self.relationshipTypeHandler.getRelationshipTypeByName(session, name)
        return dbRelationshipType.getCdbObject()
    
    @CdbDbApi.executeTransaction
    def addItem(self, domainName, name, derivedFromItemId, itemIdentifier1, itemIdentifier2, entityTypeName, qrId, description,
                createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, createdOnDataTime=None, lastModifiedOnDateTime=None, **kwargs):
        session = kwargs['session']
        dbItem = self.itemHandler.addItem(session, domainName, name, derivedFromItemId, itemIdentifier1, itemIdentifier2, entityTypeName, qrId, description,
                                          createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, createdOnDataTime, lastModifiedOnDateTime)
        return dbItem.getCdbObject()

    @CdbDbApi.executeTransaction
    def addItemElement(self, name, parentItemId, containedItemId, isRequired, description,
                createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, createdOnDataTime=None, lastModifiedOnDateTime=None, **kwargs):
        session = kwargs['session']
        dbItemElement = self.itemHandler.addItemElement(session, name, parentItemId, containedItemId, isRequired, description,
                                          createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, createdOnDataTime, lastModifiedOnDateTime)
        return dbItemElement.getCdbObject()

    @CdbDbApi.executeQuery
    def getItem(self, domainId, name, itemIdentifier1, itemIdentifier2, derivedFromItemId, **kwargs):
        session = kwargs['session']
        dbItem = self.itemHandler.getItemByUniqueAttributes(session, domainId, name, itemIdentifier1, itemIdentifier2, derivedFromItemId)
        return dbItem.getCdbObject()

    @CdbDbApi.executeQuery
    def getItemById(self, itemId, **kwargs):
        session = kwargs['session']
        dbItem = self.itemHandler.getItemById(session, itemId)
        return dbItem.getCdbObject()

    @CdbDbApi.executeQuery
    def getItemByUniqueAttributes(self, domainId, name, itemIdentifier1, itemIdentifier2, derivedFromItemId, **kwargs):
        session = kwargs['session']
        dbItem = self.itemHandler.getItemByUniqueAttributes(session, domainId, name, itemIdentifier1, itemIdentifier2, derivedFromItemId)
        return dbItem.getCdbObject()

    @CdbDbApi.executeQuery
    def getItemElementsByItemId(self, itemId, **kwargs):
        session = kwargs['session']
        dbItemElements = self.itemHandler.getItemElementsByItemId(session, itemId)
        return self.toCdbObjectList(dbItemElements)

    @CdbDbApi.executeQuery
    def getItemElementById(self, itemElementId, **kwargs):
        session = kwargs['session']
        dbItemElement = self.itemHandler.getItemElementById(session, itemElementId)
        return dbItemElement.getCdbObject()

    @CdbDbApi.executeTransaction
    def addSource(self, sourceName, description, contactInfo, url, **kwargs):
        session = kwargs['session']
        dbSource = self.sourceHandler.addSource(session, sourceName, description, contactInfo, url)
        return dbSource.getCdbObject()

    @CdbDbApi.executeQuery
    def getSources(self, **kwargs):
        session = kwargs['session']
        dbSources = self.sourceHandler.getSources(session)
        return self.toCdbObjectList(dbSources)

    @CdbDbApi.executeTransaction
    def addItemSource(self, itemId, sourceName, partNumber, cost, description, isVendor, isManufacturer, contactInfo, url, **kwargs):
        session = kwargs['session']
        dbItemSource = self.itemHandler.addItemSource(session, itemId, sourceName, partNumber, cost, description, isVendor, isManufacturer, contactInfo, url)
        return dbItemSource.getCdbObject()
    
    @CdbDbApi.executeTransaction
    def addItemCategory(self, name, description, domainName, **kwargs):
        session = kwargs['session']
        dbItemCategory = self.itemHandler.addItemCategory(session, name, description, domainName)
        return dbItemCategory.getCdbObject()

    @CdbDbApi.executeTransaction
    def addItemProject(self, itemProjectName, description, **kwargs):
        session = kwargs['session']
        dbItemCategory = self.itemHandler.addItemProject(session, itemProjectName, description)
        return dbItemCategory.getCdbObject()

    @CdbDbApi.executeQuery
    def getItemProjects(self, **kwargs):
        session = kwargs['session']
        dbItemProjects = self.itemHandler.getItemProjects(session)
        return self.toCdbObjectList(dbItemProjects)

    @CdbDbApi.executeTransaction
    def addItemItemProject(self, itemId, itemProjectName, **kwargs):
        session = kwargs['session']
        dbItemCategory = self.itemHandler.addItemItemProject(session, itemId, itemProjectName)
        return dbItemCategory.getCdbObject()
    
    @CdbDbApi.executeTransaction
    def addItemType(self, name, description, domainName, **kwargs):
        session = kwargs['session']
        dbItemType = self.itemHandler.addItemType(session, name, description, domainName)
        return dbItemType.getCdbObject()

    @CdbDbApi.executeQuery
    def getItemCategory(self, name, **kwargs):
        session = kwargs['session']
        dbItemCategory = self.itemHandler.getItemCategoryByName(session, name)
        return dbItemCategory.getCdbObject()

    @CdbDbApi.executeQuery
    def getItemType(self, name, domainName, **kwargs):
        session = kwargs['session']
        dbItemType = self.itemHandler.getItemTypeByName(session, name, domainName)
        return dbItemType.getCdbObject()

    @CdbDbApi.executeTransaction
    def addItemItemCategory(self, itemId, categoryName, **kwargs):
        session = kwargs['session']
        dbItemType = self.itemHandler.addItemItemCategory(session, itemId, categoryName)
        return dbItemType.getCdbObject()
    
    @CdbDbApi.executeTransaction
    def addItemItemType(self, itemId, typeName, **kwargs):
        session = kwargs['session']
        dbItemType = self.itemHandler.addItemItemType(session, itemId, typeName)
        return dbItemType.getCdbObject()

    @CdbDbApi.executeTransaction
    def addItemElementLog(self, itemElementId, text, enteredByUserId, effectiveFromDateTime, effectiveToDateTime, logTopicName, enteredOnDateTime = None, systemLogLevelName = None, **kwargs):
        session = kwargs['session']
        dbItemElementLog = self.itemHandler.addItemElementLog(session, itemElementId, text, enteredByUserId, effectiveFromDateTime, effectiveToDateTime, logTopicName, enteredOnDateTime, systemLogLevelName)
        return dbItemElementLog.getCdbObject()

    @CdbDbApi.executeTransaction
    def addItemElementProperty(self, itemElementId, propertyTypeName, tag, value, units, description, enteredByUserId, isUserWriteable, isDynamic, displayValue, targetValue, enteredOnDateTime = None, **kwargs):
        session = kwargs['session']
        dbItemElementProperty = self.itemHandler.addItemElementProperty(session, itemElementId, propertyTypeName, tag, value, units, description, enteredByUserId, isUserWriteable, isDynamic, displayValue, targetValue, enteredOnDateTime)
        return dbItemElementProperty.getCdbObject()

    @CdbDbApi.executeQuery
    def getSelfElementByItemId(self, itemId, **kwargs):
        session = kwargs['session']
        dbItemElement = self.itemHandler.getSelfElementByItemId(session, itemId)
        return dbItemElement.getCdbObject()

    @CdbDbApi.executeTransaction
    def addItemElementRelationship(self, firstItemElementId, secondItemElementId, firstItemConnectorId, secondItemConnectorId,
                                   linkItemElementId, relationshipTypeName, relationshipDetails, resourceTypeName, label, description, **kwargs):
        session = kwargs['session']
        dbItemElementRelationship = self.itemHandler.addItemElementRelationship(session, firstItemElementId, secondItemElementId,
                                                                                firstItemConnectorId, secondItemConnectorId,
                                                                                linkItemElementId, relationshipTypeName,
                                                                                relationshipDetails, resourceTypeName,
                                                                                label, description)
        return  dbItemElementRelationship.toCdbObject()

    @CdbDbApi.executeQuery
    def getItemsWithPropertyType(self, propertyTypeName, itemDomainName = None, itemDerivedFromItemId = None, propertyValueMatch = None, **kwargs):
        session = kwargs['session']
        dbItems = self.itemHandler.getItemsWithPropertyTypeName(session, propertyTypeName, itemDomainName, itemDerivedFromItemId, propertyValueMatch)
        return self.toCdbObjectList(dbItems)


#######################################################################
# Testing.
if __name__ == '__main__':
    api = ItemDbApi()

