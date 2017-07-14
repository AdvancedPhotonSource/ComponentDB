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


class ItemControllerImpl(CdbObjectManager):

    CATALOG_ITEM_DOMAIN_NAME = "Catalog"
    INVENTORY_ITEM_DOMAIN_NAME = "Inventory"
    LOCATION_ITEM_DOMAIN_NAME = "LOCATION"

    def __init__(self):
        CdbObjectManager.__init__(self)
        self.itemDbApi = ItemDbApi()
        self.logControllerImpl = LogControllerImpl()
        self.propertyDbApi = PropertyDbApi()

    def getItemById(self, itemId):
        return self.itemDbApi.getItemById(itemId)

    def addLogEntryForItemWithQrId(self, qrId, logEntryText, enteredByUserId, attachmentName, cherryPyData):
        item = self.itemDbApi.getItemByQrId(qrId)

        itemId = item.data['id']
        selfElement = self.itemDbApi.getSelfElementByItemId(itemId)
        selfElementId = selfElement.data['id']

        # Add log entry
        itemElementLog = self.itemDbApi.addItemElementLog(selfElementId, logEntryText, enteredByUserId)
        logEntry = itemElementLog.data['log']

        # Check if log has an attachment that needs to be stored
        if attachmentName is not None and len(attachmentName) > 0:
            logId = logEntry.data['id']
            logAttachment = self.logControllerImpl.addLogAttachment(logId, attachmentName, None, enteredByUserId, cherryPyData)
            del(logAttachment.data['log'])
            logAttachmentJsonRep = logAttachment.getFullJsonRep()
            logEntry.data['logAttachmentAdded'] = logAttachmentJsonRep

        return logEntry

    def addPropertyValueForItemWithId(self, itemId, propertyTypeName, enteredByUserId,
                                      tag=None, value=None, units=None, description=None,
                                      isUserWriteable=None, isDynamic=False):
        selfElement = self.itemDbApi.getSelfElementByItemId(itemId)
        selfElementId = selfElement.data['id']

        propertyValueAdded = self.itemDbApi.addItemElementProperty(selfElementId, propertyTypeName,
                                                                   tag, value, units, description,
                                                                   enteredByUserId, isUserWriteable, isDynamic)

        return propertyValueAdded

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

    def getItemsDerivedFromItemId(self, derivedFromItemId):
        return self.itemDbApi.getItemsDerivedFromItem(derivedFromItemId)

    def getPropertiesForItemId(self, itemId):
        selfElement = self.itemDbApi.getSelfElementByItemId(itemId)
        selfElementId = selfElement.data['id']

        return self.propertyDbApi.getPropertyValueListForItemElementId(selfElementId)

    def getDomains(self):
        return self.itemDbApi.getDomains()

    def addItem(self, domainName, name, createdByUserId, ownerUserId, ownerGroupId,
                itemIdentifier1=None, itemIdentifier2=None, qrId=None, description=None, isGroupWriteable=True, entityTypeNames=None):
        return self.itemDbApi.addItem(domainName=domainName,
                                      name=name,
                                      createdByUserId=createdByUserId,
                                      ownerUserId=ownerUserId,
                                      ownerGroupId=ownerGroupId,
                                      itemIdentifier1=itemIdentifier1,
                                      itemIdentifier2=itemIdentifier2,
                                      qrId=qrId,
                                      description=description,
                                      isGroupWriteable=isGroupWriteable,
                                      entityTypeNames=entityTypeNames)


