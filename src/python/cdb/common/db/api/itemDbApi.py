#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.db.api.cdbDbApi import CdbDbApi
from cdb.common.db.impl.entityTypeHandler import EntityTypeHandler
from cdb.common.db.impl.domainHandler import DomainHandler
from cdb.common.db.impl.itemHandler import ItemHandler
from cdb.common.db.impl.permissionHandler import PermissionHandler
from cdb.common.db.impl.sourceHandler import SourceHandler
from cdb.common.db.impl.relationshipTypeHandler import RelationshipTypeHandler

class ItemDbApi(CdbDbApi):
    """
    DB APIs that allow updates to entities related to items.
    """

    def __init__(self):
        CdbDbApi.__init__(self)
        self.entityTypeHandler = EntityTypeHandler()
        self.domainHandler = DomainHandler()
        self.itemHandler = ItemHandler()
        self.sourceHandler = SourceHandler()
        self.relationshipTypeHandler = RelationshipTypeHandler()
        self.permissionHandler = PermissionHandler()

    @CdbDbApi.executeQuery
    def getItemsOfDomain(self, domainName, **kwargs):
        session = kwargs['session']
        itemList = self.itemHandler.getItemsOfDomain(session, domainName)

        return self.toCdbObjectList(itemList)

    @CdbDbApi.executeQuery
    def getItemsDerivedFromItem(self, derivedItemId, **kwargs):
        session = kwargs['session']
        itemList = self.itemHandler.getItemsDerivedFromItem(session, derivedItemId)

        return self.toCdbObjectList(itemList)

    @CdbDbApi.executeQuery
    def verifyPermissionsForWriteToItemElement(self, username, itemElementId, **kwargs):
        """
        Check permissions for a specific item element.

        :param username:
        :param itemElementId:
        :param kwargs:
        :raises InvalidSession: when user does not have permission.
        :return: (Boolean) true if user has permissions.
        """
        session = kwargs['session']
        result = self.permissionHandler.verifyPermissionsForWriteToItemElement(session, username, itemElementId=itemElementId)
        return result

    @CdbDbApi.executeTransaction
    def addEntityType(self, name, description, **kwargs):
        """
        Add an entity type record.

        :param name:
        :param description:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbEntityType = self.entityTypeHandler.addEntityType(session, name, description)
        return dbEntityType.getCdbObject()

    @CdbDbApi.executeQuery
    def getEntityTypeByName(self, name, **kwargs):
        """
        Get an entity type record by its name.

        :param name: entity type name.
        :param kwargs:
        :return: (CdbObject) resulting record.
        """
        session = kwargs['session']
        dbEntityType = self.entityTypeHandler.findEntityTypeByName(session, name)
        return dbEntityType.getCdbObject()

    @CdbDbApi.executeTransaction
    def addAllowedChildEntityType(self, parentEntityTypeName, childEntityTypeName, **kwargs):
        """
        Add an allowed child entity type record

        :param parentEntityTypeName:
        :param childEntityTypeName:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbEntityType = self.entityTypeHandler.addAllowedChildEntityType(session, parentEntityTypeName, childEntityTypeName)
        return dbEntityType.getCdbObject()

    @CdbDbApi.executeQuery
    def getDomainByName(self, name, **kwargs):
        """
        Get an item Domain by its name.

        :param name: name of item domain
        :param kwargs:
        :return: (CdbObject) resulting record.
        """
        session = kwargs['session']
        dbDomain = self.domainHandler.findDomainByName(session, name)
        return dbDomain.getCdbObject()

    @CdbDbApi.executeTransaction
    def addAllowedEntityTypeDomain(self, domainName, entityTypeName, **kwargs):
        """
        Add an allowed entity type for a domain record.

        :param domainName:
        :param entityTypeName:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbAllowedEntityTypeDomain = self.domainHandler.addAllowedEntityType(session, domainName, entityTypeName)
        return dbAllowedEntityTypeDomain.getCdbObject()

    @CdbDbApi.executeTransaction
    def addDomain(self, name, description, **kwargs):
        """
        Add an item domain record.

        :param name:
        :param description:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbDomain = self.domainHandler.addDomain(session, name, description)
        return dbDomain.getCdbObject()

    def getDomains(self, **kwargs):
        """
        Get a list of all domains.

        :param kwargs:
        :return:
        """
        session = kwargs['session']
        dbDomainList = self.domainHandler.getDomains(session)
        return self.toCdbObjectList(dbDomainList)


    @CdbDbApi.executeTransaction
    def addRelationshipTypeHandler(self, name, description, **kwargs):
        """
        Add an item relationship type handler.

        :param name:
        :param description:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbRelationshipTypeHandler = self.relationshipTypeHandler.addRelationshipTypeHandler(session, name, description)
        return dbRelationshipTypeHandler.getCdbObject()

    @CdbDbApi.executeQuery
    def getRelationshipTypeHandlerByName(self, name, **kwargs):
        """
        Get an item relationship type handler by its name.

        :param name:
        :param kwargs:
        :return: (CdbObject) resulting record.
        """
        session = kwargs['session']
        dbRelationshipTypeHandler = self.relationshipTypeHandler.getRelationshipTypeHandlerByName(session, name)
        return dbRelationshipTypeHandler.getCdbObject()

    @CdbDbApi.executeTransaction
    def addRelationshipType(self, name, description, relationshipTypeHandlerName, **kwargs):
        """
        Add an item relationship type record.

        :param name:
        :param description:
        :param relationshipTypeHandlerName:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbRelationshipType = self.relationshipTypeHandler.addRelationshipType(session, name, description, relationshipTypeHandlerName)
        return dbRelationshipType.getCdbObject()

    @CdbDbApi.executeQuery
    def getRelationshipTypeByName(self, name, **kwargs):
        """
        Get an item relationship type record by its name.

        :param name:
        :param kwargs:
        :return: (CdbObject) resulting record.
        """
        session = kwargs['session']
        dbRelationshipType = self.relationshipTypeHandler.getRelationshipTypeByName(session, name)
        return dbRelationshipType.getCdbObject()
    
    @CdbDbApi.executeTransaction
    def addItem(self, domainName, name, createdByUserId, ownerUserId, ownerGroupId,
                itemIdentifier1 = None, itemIdentifier2 = None, qrId = None, description = None,
                isGroupWriteable=True, createdOnDataTime=None, lastModifiedOnDateTime=None,
                derivedFromItemId=None, entityTypeNames=None, **kwargs):
        """
        Add an item record.

        :param domainName:
        :param name:
        :param createdByUserId:
        :param ownerUserId:
        :param ownerGroupId:
        :param itemIdentifier1:
        :param itemIdentifier2:
        :param qrId:
        :param isGroupWriteable:
        :param createdOnDataTime:
        :param lastModifiedOnDateTime:
        :param derivedFromItemId:
        :param entityTypeNames:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbItem = self.itemHandler.addItem(session, domainName, name, createdByUserId, ownerUserId, ownerGroupId,
                                          itemIdentifier1, itemIdentifier2, qrId, description, isGroupWriteable,
                                          createdOnDataTime, lastModifiedOnDateTime, derivedFromItemId, entityTypeNames)
        return dbItem.getCdbObject()

    @CdbDbApi.executeTransaction
    def addItemElement(self, name, parentItemId, containedItemId, isRequired, description,
                createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, createdOnDataTime=None, lastModifiedOnDateTime=None, **kwargs):
        """
        Add an item element record.

        :param name:
        :param parentItemId:
        :param containedItemId:
        :param isRequired:
        :param description:
        :param createdByUserId:
        :param ownerUserId:
        :param ownerGroupId:
        :param isGroupWriteable:
        :param createdOnDataTime:
        :param lastModifiedOnDateTime:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbItemElement = self.itemHandler.addItemElement(session, name, parentItemId, containedItemId, isRequired, description,
                                          createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, createdOnDataTime, lastModifiedOnDateTime)
        return dbItemElement.getCdbObject()

    @CdbDbApi.executeTransaction
    def updateItemElement(self, itemElementId, lastModifiedUserId, containedItemId=-1, isRequired=-1,
                          name=None, description=None, ownerUserId=None, ownerGroupId=None, isGroupWriteable=None, **kwargs):
        """
        Update an item element record.

        :param itemElementId:
        :param lastModifiedUserId:
        :param containedItemId:
        :param isRequired:
        :param name:
        :param description:
        :param ownerUserId:
        :param ownerGroupId:
        :param isGroupWriteable:
        :return:
        """

        session = kwargs['session']
        dbItemElement = self.itemHandler.updateItemElement(session, itemElementId, lastModifiedUserId, containedItemId,
                                                           isRequired, name, description, ownerUserId, ownerGroupId, isGroupWriteable)

        return dbItemElement.getCdbObject()


    @CdbDbApi.executeQuery
    def getItemById(self, itemId, **kwargs):
        """
        Get an item record by its id.

        :param itemId:
        :param kwargs:
        :return: (CdbObject) resulting record.
        """
        session = kwargs['session']
        dbItem = self.itemHandler.getItemById(session, itemId)
        return dbItem.getCdbObject()

    @CdbDbApi.executeQuery
    def getItemByQrId(self, itemQrId, **kwargs):
        """
        Get an item record by its id.

        :param itemQrId:
        :param kwargs:
        :return: (CdbObject) resulting record.
        """
        session = kwargs['session']
        dbItem = self.itemHandler.getItemByQrId(session, itemQrId)
        return dbItem.getCdbObject()

    @CdbDbApi.executeQuery
    def getItemByUniqueAttributes(self, domainId, name, itemIdentifier1, itemIdentifier2, derivedFromItemId, **kwargs):
        """
        Get an item record based on its unique attributes.

        :param domainId:
        :param name:
        :param itemIdentifier1:
        :param itemIdentifier2:
        :param derivedFromItemId:
        :param kwargs:
        :return: (CdbObject) resulting record.
        """
        session = kwargs['session']
        dbItem = self.itemHandler.getItemByUniqueAttributes(session, domainId, name, itemIdentifier1, itemIdentifier2, derivedFromItemId)
        return dbItem.getCdbObject()

    @CdbDbApi.executeQuery
    def getItemElementsByItemId(self, itemId, **kwargs):
        """
        Get a list of item elements for a particular item.

        :param itemId:
        :param kwargs:
        :return: CdbObject List of item elements of a particular item.
        """
        session = kwargs['session']
        dbItemElements = self.itemHandler.getItemElementsByItemId(session, itemId)
        return self.toCdbObjectList(dbItemElements)

    @CdbDbApi.executeQuery
    def getItemElementById(self, itemElementId, **kwargs):
        """
        Get an item element by its id.

        :param itemElementId:
        :param kwargs:
        :return: (CdbObject) resulting record.
        """
        session = kwargs['session']
        dbItemElement = self.itemHandler.getItemElementById(session, itemElementId)
        return dbItemElement.getCdbObject()

    @CdbDbApi.executeTransaction
    def addSource(self, sourceName, description, contactInfo, url, **kwargs):
        """
        Add a source record.

        Sources are various vendors that various items could be obtained from.

        :param sourceName:
        :param description:
        :param contactInfo:
        :param url:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbSource = self.sourceHandler.addSource(session, sourceName, description, contactInfo, url)
        return dbSource.getCdbObject()

    @CdbDbApi.executeQuery
    def getSources(self, **kwargs):
        """
        Get all records of sources.

        :param kwargs:
        :return: CDbObject List of sources in the db.
        """
        session = kwargs['session']
        dbSources = self.sourceHandler.getSources(session)
        return self.toCdbObjectList(dbSources)

    @CdbDbApi.executeTransaction
    def addItemSource(self, itemId, sourceName, partNumber, cost, description, isVendor, isManufacturer, contactInfo, url, **kwargs):
        """
        Add an item source record.

        :param itemId:
        :param sourceName:
        :param partNumber:
        :param cost:
        :param description:
        :param isVendor:
        :param isManufacturer:
        :param contactInfo:
        :param url:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """

        session = kwargs['session']
        dbItemSource = self.itemHandler.addItemSource(session, itemId, sourceName, partNumber, cost, description, isVendor, isManufacturer, contactInfo, url)
        return dbItemSource.getCdbObject()
    
    @CdbDbApi.executeTransaction
    def addItemCategory(self, name, description, domainName, **kwargs):
        """
        Add an item category

        items in particular domain could have different categories.

        :param name:
        :param description:
        :param domainName:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbItemCategory = self.itemHandler.addItemCategory(session, name, description, domainName)
        return dbItemCategory.getCdbObject()

    @CdbDbApi.executeTransaction
    def addItemProject(self, itemProjectName, description, **kwargs):
        """
        Add an item project.

        Items are assigned to projects.

        :param itemProjectName:
        :param description:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbItemCategory = self.itemHandler.addItemProject(session, itemProjectName, description)
        return dbItemCategory.getCdbObject()

    @CdbDbApi.executeQuery
    def getItemProjects(self, **kwargs):
        """
        Get all item project records in the db.

        :param kwargs:
        :return: CDBObject List of item projects.
        """
        session = kwargs['session']
        dbItemProjects = self.itemHandler.getItemProjects(session)
        return self.toCdbObjectList(dbItemProjects)

    @CdbDbApi.executeTransaction
    def addItemItemProject(self, itemId, itemProjectName, **kwargs):
        """
        Assign an item to a particular project.

        :param itemId:
        :param itemProjectName:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """

        session = kwargs['session']
        dbItemCategory = self.itemHandler.addItemItemProject(session, itemId, itemProjectName)
        return dbItemCategory.getCdbObject()
    
    @CdbDbApi.executeTransaction
    def addItemType(self, name, description, domainName, **kwargs):
        """
        Add an item type record.

        items in particular domain could have different types.

        :param name:
        :param description:
        :param domainName:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbItemType = self.itemHandler.addItemType(session, name, description, domainName)
        return dbItemType.getCdbObject()

    @CdbDbApi.executeQuery
    def getItemCategory(self, name, **kwargs):
        """
        Get an item category by its name.

        :param name:
        :param kwargs:
        :return: (CdbObject) resulting record.
        """
        session = kwargs['session']
        dbItemCategory = self.itemHandler.getItemCategoryByName(session, name)
        return dbItemCategory.getCdbObject()

    @CdbDbApi.executeQuery
    def getItemType(self, name, domainName, **kwargs):
        """
        Get an item type by its name and domain name.

        :param name:
        :param domainName:
        :param kwargs:
        :return: (CdbObject) resulting record.
        """
        session = kwargs['session']
        dbItemType = self.itemHandler.getItemTypeByName(session, name, domainName)
        return dbItemType.getCdbObject()

    @CdbDbApi.executeTransaction
    def addItemItemCategory(self, itemId, categoryName, **kwargs):
        """
        Assign an item category to a particular item.

        :param itemId:
        :param categoryName:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbItemType = self.itemHandler.addItemItemCategory(session, itemId, categoryName)
        return dbItemType.getCdbObject()
    
    @CdbDbApi.executeTransaction
    def addItemItemType(self, itemId, typeName, **kwargs):
        """
        Assign an item type to a particular item.

        :param itemId:
        :param typeName:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbItemType = self.itemHandler.addItemItemType(session, itemId, typeName)
        return dbItemType.getCdbObject()

    @CdbDbApi.executeTransaction
    def addItemElementLog(self, itemElementId, text, enteredByUserId, effectiveFromDateTime = None, effectiveToDateTime = None, logTopicName = None, enteredOnDateTime = None, systemLogLevelName = None, **kwargs):
        """
        Add a log to a particular item element.

        NOTE: items have logs through their 'self element'.

        :param itemElementId:
        :param text:
        :param enteredByUserId:
        :param effectiveFromDateTime:
        :param effectiveToDateTime:
        :param logTopicName:
        :param enteredOnDateTime:
        :param systemLogLevelName:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbItemElementLog = self.itemHandler.addItemElementLog(session, itemElementId, text, enteredByUserId, effectiveFromDateTime, effectiveToDateTime, logTopicName, enteredOnDateTime, systemLogLevelName)
        return dbItemElementLog.getCdbObject()

    @CdbDbApi.executeTransaction
    def addItemElementProperty(self, itemElementId, propertyTypeName, tag=None, value=None, units=None,
                               description=None, enteredByUserId=None, isUserWriteable=False, isDynamic=False,
                               displayValue=None, targetValue=None, enteredOnDateTime = None, **kwargs):
        """
        Add a property to a particular item element.

        NOTE: items have properties through their 'self element'.

        :param itemElementId:
        :param propertyTypeName:
        :param tag:
        :param value:
        :param units:
        :param description:
        :param enteredByUserId:
        :param isUserWriteable:
        :param isDynamic:
        :param displayValue:
        :param targetValue:
        :param enteredOnDateTime:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbItemElementProperty = self.itemHandler.addItemElementProperty(session, itemElementId, propertyTypeName, tag, value, units, description, enteredByUserId, isUserWriteable, isDynamic, displayValue, targetValue, enteredOnDateTime)
        return dbItemElementProperty.getCdbObject()

    @CdbDbApi.executeQuery
    def getSelfElementByItemId(self, itemId, **kwargs):
        """
        Get an item self element by the id of the item.

        :param itemId:
        :param kwargs:
        :return: (CdbObject) resulting record.
        """
        session = kwargs['session']
        dbItemElement = self.itemHandler.getSelfElementByItemId(session, itemId)
        return dbItemElement.getCdbObject()

    @CdbDbApi.executeTransaction
    def addItemElementRelationship(self, firstItemElementId, secondItemElementId, firstItemConnectorId, secondItemConnectorId,
                                   linkItemElementId, relationshipTypeName, relationshipDetails, resourceTypeName, label, description, **kwargs):
        """
        Add an item element relationship.

        :param firstItemElementId:
        :param secondItemElementId:
        :param firstItemConnectorId:
        :param secondItemConnectorId:
        :param linkItemElementId:
        :param relationshipTypeName:
        :param relationshipDetails:
        :param resourceTypeName:
        :param label:
        :param description:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbItemElementRelationship = self.itemHandler.addItemElementRelationship(session, firstItemElementId, secondItemElementId,
                                                                                firstItemConnectorId, secondItemConnectorId,
                                                                                linkItemElementId, relationshipTypeName,
                                                                                relationshipDetails, resourceTypeName,
                                                                                label, description)
        return  dbItemElementRelationship.toCdbObject()

    @CdbDbApi.executeQuery
    def getItemsWithPropertyType(self, propertyTypeName, itemDomainName = None, itemDerivedFromItemId = None, propertyValueMatch = None, **kwargs):
        """
        Fetches items that have a particular property type.

        :param propertyTypeName:
        :param itemDomainName:
        :param itemDerivedFromItemId:
        :param propertyValueMatch:
        :param kwargs:
        :return: (CdbObject) resulting record.
        """
        session = kwargs['session']
        dbItems = self.itemHandler.getItemsWithPropertyTypeName(session, propertyTypeName, itemDomainName, itemDerivedFromItemId, propertyValueMatch)
        return self.toCdbObjectList(dbItems)

    @CdbDbApi.executeQuery
    def getParentItems(self, itemId, **kwargs):
        """
        Fetches a list of items who contained the item specified as input.

        :param itemId:
        :param kwargs:
        :return:
        """
        session = kwargs['session']
        dbItems = self.itemHandler.getParentItems(session, itemId)
        return self.toCdbObjectList(dbItems)


#######################################################################
# Testing.
if __name__ == '__main__':
    api = ItemDbApi()

