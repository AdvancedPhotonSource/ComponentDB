#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Implementation for the Item class
#

#######################################################################
from cdb.cdb_web_service.impl.logControllerImpl import LogControllerImpl
from cdb.common.db.api.propertyDbApi import PropertyDbApi
from cdb.common.objects.cdbObjectManager import CdbObjectManager
from cdb.common.db.api.itemDbApi import ItemDbApi
from cdb.common.utility.storageUtility import StorageUtility


class ItemControllerImpl(CdbObjectManager):

    CATALOG_ITEM_DOMAIN_NAME = "Catalog"
    INVENTORY_ITEM_DOMAIN_NAME = "Inventory"
    LOCATION_ITEM_DOMAIN_NAME = "LOCATION"

    def __init__(self):
        CdbObjectManager.__init__(self)
        self.itemDbApi = ItemDbApi()
        self.logControllerImpl = LogControllerImpl()
        self.propertyDbApi = PropertyDbApi()
        self.storageUtility = StorageUtility.getInstance()

    def getItemById(self, itemId):
        return self.itemDbApi.getItemById(itemId)

    def getItemByQrId(self, itemQrId):
        return self.itemDbApi.getItemByQrId(itemQrId)

    def getFirstItemRelationship(self, itemId, relationshipTypeName):
        selfElement = self.itemDbApi.getSelfElementByItemId(itemId)
        selfElementId = selfElement.data['id']

        return self.itemDbApi.getFirstItemElementRelationshipList(relationshipTypeName, selfElementId)

    def addItemElementRelationshipByQrId(self, firstItemQrId, secondItemQrId, relationshipTypeName, enteredByUserId,
                                   relationshipDetails=None, description=None):
        firstItemId = self.itemDbApi.getItemByQrId(firstItemQrId).data['id']
        secondItemId = self.itemDbApi.getItemByQrId(secondItemQrId).data['id']

        return self.__addItemElementRelationship(firstItemId, secondItemId, relationshipTypeName, enteredByUserId,
                                                 relationshipDetails, description)


    def addItemElementRelationship(self, firstItemId, secondItemId, relationshipTypeName, enteredByUserId,
                                   relationshipDetails=None, description=None):
        return self.__addItemElementRelationship(firstItemId, secondItemId, relationshipTypeName, enteredByUserId,
                                   relationshipDetails, description)

    def __addItemElementRelationship(self, firstItemId, secondItemId, relationshipTypeName, enteredByUserId,
                                   relationshipDetails=None, description=None):
        firstSelfElement = self.itemDbApi.getSelfElementByItemId(firstItemId)
        firstSelfElementId = firstSelfElement.data['id']
        secondSelfElement = self.itemDbApi.getSelfElementByItemId(secondItemId)
        secondSelfElementId = secondSelfElement.data['id']

        return self.itemDbApi.addValidItemElementRelationship(firstSelfElementId,
                                                              secondSelfElementId,
                                                              relationshipTypeName,
                                                              enteredByUserId,
                                                              relationshipDetails,
                                                              description)


    def getItemByUniqueAttributes(self, domainName, itemName, itemIdentifier1=None, itemIdentifier2=None, derivedFromItemId=None):
        domain = self.itemDbApi.getDomainByName(domainName)
        return self.itemDbApi.getItemByUniqueAttributes(domain.data['id'], itemName, itemIdentifier1, itemIdentifier2, derivedFromItemId)

    def addLogEntryForItemWithQrId(self, qrId, logEntryText, enteredByUserId, attachmentName, cherryPyData):
        item = self.itemDbApi.getItemByQrId(qrId)

        itemId = item.data['id']
        return self.addLogEntryForItemWithItemId(itemId, logEntryText, enteredByUserId, attachmentName, cherryPyData)

    def addLogEntryForItemWithItemId(self, itemId, logEntryText, enteredByUserId, attachmentName, cherryPyData):
        selfElement = self.itemDbApi.getSelfElementByItemId(itemId)
        selfElementId = selfElement.data['id']

        # Add log entry
        itemElementLog = self.itemDbApi.addItemElementLog(selfElementId, logEntryText, enteredByUserId)
        logEntry = itemElementLog.data['log']

        # Check if log has an attachment that needs to be stored
        if attachmentName is not None and len(attachmentName) > 0:
            logId = logEntry.data['id']
            logAttachment = self.logControllerImpl.addLogAttachment(logId, attachmentName, None, enteredByUserId,
                                                                    cherryPyData)
            del (logAttachment.data['log'])
            logAttachmentJsonRep = logAttachment.getFullJsonRep()
            logEntry.data['logAttachmentAdded'] = logAttachmentJsonRep

        return logEntry


    def addPropertyImageToItem(self, itemId, imageFileName, enteredByUserId, cherryPyData):
        selfElement = self.itemDbApi.getSelfElementByItemId(itemId)
        selfElementId = selfElement.data['id']

        if self.itemDbApi.verifyPermissionsForWriteToItemElement(enteredByUserId, selfElementId):
            storedAttachmentName = self.storageUtility.storePropertyImage(cherryPyData, imageFileName)

            propertyValue = self.itemDbApi.addItemElementImageProperty(selfElementId, enteredByUserId, storedAttachmentName, imageFileName)

            return propertyValue

        return None


    def addPropertyValueForItemWithId(self, itemId, propertyTypeName, enteredByUserId,
                                      tag=None, value=None, units=None, description=None,
                                      isUserWriteable=None, isDynamic=False, displayValue=None):
        selfElement = self.itemDbApi.getSelfElementByItemId(itemId)
        selfElementId = selfElement.data['id']

        propertyValueAdded = self.itemDbApi.addItemElementProperty(selfElementId, propertyTypeName,
                                                                   tag, value, units, description,
                                                                   enteredByUserId, isUserWriteable, isDynamic, displayValue=displayValue)

        return propertyValueAdded

    def getParentItems(self, itemId):
        return self.itemDbApi.getParentItems(itemId)

    def getItemElementsForItem(self, itemId):
        return self.itemDbApi.getItemElementsByItemId(itemId)

    def getLogEntriesForItemWithQrId(self, qrId):
        item = self.itemDbApi.getItemByQrId(qrId)
        itemId = item.data['id']
        selfElement = self.itemDbApi.getSelfElementByItemId(itemId)
        selfElementId = selfElement.data['id']

        return self.logControllerImpl.getLogEntriesForItemElement(selfElementId)

    def getLogEntriesForItemWithId(self, itemId):
        selfElement = self.itemDbApi.getSelfElementByItemId(itemId)
        selfElementId = selfElement.data['id']
        return self.logControllerImpl.getLogEntriesForItemElement(selfElementId)

    def getCatalogItems(self):
        return self.itemDbApi.getItemsOfDomain(self.CATALOG_ITEM_DOMAIN_NAME)

    def getLocationItems(self):
        return self.itemDbApi.getItemsOfDomain(self.LOCATION_ITEM_DOMAIN_NAME)

    def getLocationItemsWithoutParents(self):
        return self.itemDbApi.getItemsOfDomainWithoutParents(self.LOCATION_ITEM_DOMAIN_NAME)

    def getItemsDerivedFromItemId(self, derivedFromItemId):
        return self.itemDbApi.getItemsDerivedFromItem(derivedFromItemId)

    def getPropertiesForItemId(self, itemId):
        selfElement = self.itemDbApi.getSelfElementByItemId(itemId)
        selfElementId = selfElement.data['id']

        return self.propertyDbApi.getPropertyValueListForItemElementId(selfElementId)

    def getDomains(self):
        return self.itemDbApi.getDomains()

    def addItem(self, domainName, name, createdByUserId, ownerUserId, ownerGroupId, itemProjectName=None, itemIdentifier1=None,
                itemIdentifier2=None, qrId=None, description=None, isGroupWriteable=True, entityTypeNames=None, derivedFromItemId=None):
        return self.itemDbApi.addItem(domainName=domainName,
                                      name=name,
                                      createdByUserId=createdByUserId,
                                      ownerUserId=ownerUserId,
                                      ownerGroupId=ownerGroupId,
                                      itemProjectName=itemProjectName,
                                      itemIdentifier1=itemIdentifier1,
                                      itemIdentifier2=itemIdentifier2,
                                      qrId=qrId,
                                      description=description,
                                      isGroupWriteable=isGroupWriteable,
                                      entityTypeNames=entityTypeNames,
                                      derivedFromItemId=derivedFromItemId)

    def getAvailableInventoryItemStatuses(self):
        return self.itemDbApi.getAvailableInventoryItemStatuses()

    def getInventoryItemStatus(self, itemId):
        return self.itemDbApi.getInventoryItemStatus(itemId)

    def updateInventoryItemStatus(self, itemId, statusName, enteredByUserId):
        return self.itemDbApi.updateInventoryItemStatus(itemId, statusName, enteredByUserId)


