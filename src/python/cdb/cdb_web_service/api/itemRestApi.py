#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.objects.allowedPropertyValue import AllowedPropertyValue
from cdb.common.utility.encoder import Encoder
from cdb.common.objects.domain import Domain
from cdb.common.objects.log import Log
from cdb.common.objects.item import Item
from cdb.common.objects.itemElement import ItemElement
from cdb.common.objects.itemElementProperty import ItemElementProperty
from cdb.common.objects.propertyValue import PropertyValue
from cdb.common.objects.itemElementRelationship import ItemElementRelationship
from cdb.common.api.cdbRestApi import CdbRestApi


class ItemRestApi(CdbRestApi):
    def __init__(self, username=None, password=None, host=None, port=None, protocol=None):
        CdbRestApi.__init__(self, username, password, host, port, protocol)

    def addLogEntryToItemWithQrId(self, qrId, logEntry, attachment=None):
        if qrId is not None:
            qrId = str(qrId)
        if qrId is None or not len(qrId):
            raise InvalidRequest("QrId must be provided")

        url = '%s/items/%s/addLogEntry' % (self.getContextRoot(), qrId)

        return self.__finalizeAddLogEntryToItem(url, logEntry, attachment)

    def addLogEntryToItemWithItemId(self, itemId, logEntry, attachment=None):
        if itemId is not None:
            itemId = str(itemId)
        if itemId is None or not len(itemId):
            raise InvalidRequest("Item Id must be provided")

        url = '%s/items/%s/addLogEntryByItemId' % (self.getContextRoot(), itemId)

        return self.__finalizeAddLogEntryToItem(url, logEntry, attachment)

    def __finalizeAddLogEntryToItem(self, url, logEntry, attachment=None):
        if logEntry is None or not len(logEntry):
            raise InvalidRequest('Log entry must be provided.')

        url += '?logEntry=%s' % Encoder.encode(logEntry)

        if attachment is not None and len(attachment) > 0:
            fileName, data = self._generateFileData(attachment)
            url += '&attachmentName=%s' % Encoder.encode(fileName)
            responseDict = self.sendSessionRequest(url=url, method='POST', data=data)
        else:
            responseDict = self.sendSessionRequest(url=url, method='POST')

        return Log(responseDict)


    def addImagePropertyToItemWithId(self, itemId, imageFile):

        url = '%s/items/%s/addImageProperty' % (self.getContextRoot(), itemId)

        fileName, data = self._generateFileData(imageFile)
        fileName = Encoder.encode(fileName)
        url = self._appendUrlParameter(url, 'imageFileName', fileName)

        response = self.sendSessionRequest(url=url, method='POST', data=data)

        return PropertyValue(response)


    def __createAddPropertyRequest(self, url, id, propertyTypeName, tag=None, value=None, units=None, description=None,
                                      isUserWriteable=None, isDynamic=None, displayValue=None):
        if propertyTypeName is None or not len(propertyTypeName):
            raise InvalidRequest("propertyTypeName must be provided")

        propertyTypeName = Encoder.encode(propertyTypeName)
        url = url % (self.getContextRoot(), id, propertyTypeName)

        if tag is not None:
            tag = Encoder.encode(tag)
            url = self._appendUrlParameter(url, 'tag', tag)

        if value is not None:
            value = Encoder.encode(value)
            url = self._appendUrlParameter(url, 'value', value)

        if displayValue is not None:
            displayValue = Encoder.encode(displayValue)
            url = self._appendUrlParameter(url, 'displayValue', displayValue)

        if units is not None:
            units = Encoder.encode(units)
            url = self._appendUrlParameter(url, 'units', units)

        if description is not None:
            description = Encoder.encode(description)
            url = self._appendUrlParameter(url, 'description', description)

        if isUserWriteable is not None:
            url = self._appendUrlParameter(url, 'isUserWriteable', isUserWriteable)

        if isDynamic is not None:
            url = self._appendUrlParameter(url, 'isDynamc', isDynamic)

        responseDict = self.sendSessionRequest(url=url, method='POST')

        return PropertyValue(responseDict)

    def addPropertyValueToItemWithId(self, itemId, propertyTypeName, tag=None, value=None, units=None, description=None,
                                      isUserWriteable=None, isDynamic=None, displayValue=None):
        if itemId is not None:
            itemId = str(itemId)
        if itemId is None or not len(itemId):
            raise InvalidRequest("itemId must be provided")

        url = '%s/items/%s/addPropertyValue/%s'

        return self.__createAddPropertyRequest(url,  itemId, propertyTypeName, tag, value, units, description,
                                      isUserWriteable, isDynamic, displayValue)

    def addPropertyValueToItemElementWithId(self, itemElementId, propertyTypeName, tag=None, value=None, units=None,
                                            description=None, isUserWriteable=None, isDynamic=None, displayValue=None):
        if itemElementId is not None:
            itemElementId = str(itemElementId)
        if itemElementId is None or not len(itemElementId):
            raise InvalidRequest("itemElementId must be provided")

        url = '%s/itemElements/%s/addPropertyValue/%s'

        return self.__createAddPropertyRequest(url, itemElementId, propertyTypeName, tag, value, units, description,
                                      isUserWriteable, isDynamic, displayValue)

    def deletePropertyValuesFromItemWithId(self, itemId, propertyTypeName):
        if itemId is not None:
            itemId = str(itemId)
        if itemId is None or not len(itemId):
            raise InvalidRequest("itemId must be provided")
        if propertyTypeName is None or not len(propertyTypeName):
            raise InvalidRequest("propertyTypeName must be provided")
        propertyTypeName = Encoder.encode(propertyTypeName)
        url = '%s/items/%s/deletePropertyValues/%s'
        url = url % (self.getContextRoot(), itemId, propertyTypeName)
        responseData = self.sendSessionRequest(url=url, method='DELETE')
        return self.toCdbObjectList(responseData, PropertyValue)

    def getLogEntriesForItemWithQrId(self, qrId):
        if qrId is not None:
            qrId = str(qrId)
        if qrId is None or not len(qrId):
            raise InvalidRequest("QrId must be provided")

        url = '%s/items/%s/logs' % (self.getContextRoot(), qrId)

        responseData = self.sendRequest(url, method='GET')
        return self.toCdbObjectList(responseData, Log)

    def getItemById(self, itemId):
        if itemId is not None:
            itemId = str(itemId)
        if itemId is None or not len(itemId):
            raise InvalidRequest("itemId must be provided")
        
        url = '%s/items/%s' % (self.getContextRoot(), itemId)
        
        responseData = self.sendRequest(url=url, method='GET')
        return Item(responseData)

    def getItemByQrId(self, itemQrId):
        if itemQrId is not None:
            itemQrId = str(itemQrId)
        if itemQrId is None or not len(itemQrId):
            raise InvalidRequest("itemQrId must be provided")

        url = '%s/items/%s/qrId' % (self.getContextRoot(), itemQrId)

        responseData = self.sendRequest(url=url, method='GET')
        return Item(responseData)

    def getItemByUniqueAttributes(self, domainName, itemName, itemIdentifier1=None, itemIdentifier2=None, derivedFromItemId=None):
        if domainName is None or not len(domainName):
            raise InvalidRequest("domainName must be provided")

        if itemName is None or not len(itemName):
            raise InvalidRequest("itemName must be provided")

        domainName = Encoder.encode(domainName)
        itemName = Encoder.encode(itemName)
        url = '%s/items/uniqueAttributes/%s/%s' % (self.getContextRoot(), domainName, itemName)

        if itemIdentifier1 is not None:
            itemIdentifier1 = Encoder.encode(str(itemIdentifier1))
            url = self._appendUrlParameter(url, "itemIdentifier1", itemIdentifier1)

        if itemIdentifier2 is not None:
            itemIdentifier2 = Encoder.encode(str(itemIdentifier2))
            url = self._appendUrlParameter(url, "itemIdentifier2", itemIdentifier2)

        if derivedFromItemId is not None:
            url = self._appendUrlParameter(url, "derivedFromItemId", derivedFromItemId)

        responseData = self.sendRequest(url=url, method="GET")
        return Item(responseData)

    def getItemElementById(self, ItemElementId):
        if ItemElementId is not None:
            ItemElementId = str(ItemElementId)
        if ItemElementId is None or not len(ItemElementId):
            raise InvalidRequest("ItemElementId must be provided")

        url = '%s/itemElements/%s' % (self.getContextRoot(), ItemElementId)

        responseData = self.sendRequest(url=url, method='GET')
        return ItemElement(responseData)

    def getLogsForItemByItemQrId(self, qrId):
        if qrId is not None:
            qrId = str(qrId)
        if qrId is None or not len(qrId):
            raise InvalidRequest("qrId must be provided")

        url = '%s/items/%s/logs' % (self.getContextRoot(), qrId)

        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, Log)
    
    def getLogsForItemByItemId(self, itemId):
        if itemId is not None:
            itemId = str(itemId)
        if itemId is None or not len(itemId):
            raise InvalidRequest("itemId must be provided")

        url = '%s/items/%s/logsByItemId' % (self.getContextRoot(), itemId)

        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, Log)

    def getPropertiesForItemByItemId(self, itemId):
        if itemId is not None:
            itemId = str(itemId)
        if itemId is None or not len(itemId):
            raise InvalidRequest("itemId must be provided")
        
        url = '%s/items/%s/propertiesByItemId' % (self.getContextRoot(), itemId)

        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, PropertyValue)

    def getItemsDerivedFromItem(self, itemId):
        if itemId is not None:
            itemId = str(itemId)
        if itemId is None or not len(itemId):
            raise InvalidRequest("itemId must be provided")
        url= '%s/items/derivedFromItem/%s' % (self.getContextRoot(), itemId)

        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, Item)

    def getCatalogItems(self):
        url = '%s/items/domain/catalog' % self.getContextRoot()

        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, Item)

    def getLocationItems(self):
        url = '%s/items/domain/location' % self.getContextRoot()

        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, Item)

    def getLocationTopLevelItems(self):
        url = '%s/items/domain/location/topLevel' % self.getContextRoot()

        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, Item)

    def getDomains(self):
        url = '%s/domains' % self.getContextRoot()
        responseData =self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, Domain)

    def addItem(self, domainName, name, itemProjectName=None, itemIdentifier1=None, itemIdentifier2=None, qrId=None, description=None,
                ownerUserId=None, ownerGroupId=None, isGroupWriteable=None, entityTypeNames=None, derivedFromItemId=None):
        if domainName is None or not len(domainName):
            raise InvalidRequest("domainName must be provided")
        
        if name is None or not len(name):
            raise InvalidRequest("name must be provided")
        
        name = Encoder.encode(name)
        domainName = Encoder.encode(domainName)

        url = '%s/items/add/%s/domain/%s' % (self.getContextRoot(), name, domainName)

        if itemIdentifier1 is not None:
            itemIdentifier1 = Encoder.encode(itemIdentifier1)
            url = self._appendUrlParameter(url, 'itemIdentifier1', itemIdentifier1)
            
        if itemIdentifier2 is not None:
            itemIdentifier2 = Encoder.encode(itemIdentifier2)
            url = self._appendUrlParameter(url, 'itemIdentifier2', itemIdentifier2)
            
        if qrId is not None:
            url = self._appendUrlParameter(url, 'qrId', qrId)
            
        if description is not None:
            description = Encoder.encode(description)
            url = self._appendUrlParameter(url, 'description', description)
        
        if ownerUserId is not None:
            url = self._appendUrlParameter(url, 'ownerUserId', ownerUserId)
            
        if ownerGroupId is not None:
            url = self._appendUrlParameter(url, 'ownerGroupId', ownerGroupId)

        if isGroupWriteable is not None:
            url = self._appendUrlParameter(url, 'isGroupWriteable', isGroupWriteable)

        if entityTypeNames is not None:
            entityTypeNames = Encoder.encode(str(entityTypeNames))
            url = self._appendUrlParameter(url, "entityTypeNames", entityTypeNames)

        if derivedFromItemId is not None:
            url = self._appendUrlParameter(url, "derivedFromItemId", derivedFromItemId)

        if itemProjectName is not None:
            itemProjectName = Encoder.encode(itemProjectName)
            url = self._appendUrlParameter(url, 'itemProjectName', itemProjectName)
            
        responseData = self.sendSessionRequest(url, method='POST')

        return Item(responseData)

    def addItemRelationshipByQrId(self, firstItemQrId, secondItemQrId, relationshipTypeName,
                            relationshipDetails=None, description=None):
        if firstItemQrId is None:
            raise InvalidRequest("first item qr id must be provided")
        if secondItemQrId is None:
            raise InvalidRequest("second item qr id must be provided")

        relationshipTypeName = Encoder.encode(relationshipTypeName)

        url = "%s/items/%s/%s/addItemElementRelationshipByQrId/%s" \
              % (self.getContextRoot(), firstItemQrId, secondItemQrId, relationshipTypeName)

        return self.__finalizeAddItemRelationship(url, relationshipDetails, description)


    def addItemRelationship(self, firstItemId, secondItemId, relationshipTypeName,
                            relationshipDetails=None, description=None):
        if firstItemId is None:
            raise InvalidRequest("first item id must be provided")
        if secondItemId is None:
            raise InvalidRequest("second item id must be provided")
        if relationshipTypeName is None or not len(relationshipTypeName):
            raise InvalidRequest("relationship type name must be provided")

        relationshipTypeName = Encoder.encode(relationshipTypeName)

        url = "%s/items/%s/%s/addItemElementRelationship/%s" \
              % (self.getContextRoot(), firstItemId, secondItemId, relationshipTypeName)

        return self.__finalizeAddItemRelationship(url, relationshipDetails, description)

    def __finalizeAddItemRelationship(self, url, relationshipDetails, description):
        if relationshipDetails is not None:
            relationshipDetails = Encoder.encode(relationshipDetails)
            url = self._appendUrlParameter(url, "relationshipDetails", relationshipDetails)

        if description is not None:
            description = Encoder.encode(description)
            url = self._appendUrlParameter(url, "description", description)

        responseData = self.sendSessionRequest(url=url, method='POST')
        return ItemElementRelationship(responseData)

    def getFirstItemRelationshipList(self, itemId, relationshipTypeName):
        if itemId is None:
            raise InvalidRequest("item id must be provided")
        if relationshipTypeName is None or not len(relationshipTypeName):
            raise InvalidRequest("relationship type name must be provided")

        relationshipTypeName = Encoder.encode(relationshipTypeName)

        url = "%s/items/%s/firstItemElementRelationships/%s" % (self.getContextRoot(), itemId, relationshipTypeName)

        responseData = self.sendRequest(url=url, method="GET")
        return self.toCdbObjectList(responseData, ItemElementRelationship)


    def addItemElement(self, itemElementName, parentItemId, containedItemId = -1, description=None,
                       ownerUserId=None, ownerGroupId=None, isRequired=-1, isGroupWriteable=None):
        if parentItemId is not None:
            parentItemId = str(parentItemId)

        if parentItemId is None or not len(parentItemId):
            raise InvalidRequest("parentItemId must be provided")

        if itemElementName is None or not len(itemElementName):
            raise InvalidRequest("itemElementName must be provided")

        itemElementName = Encoder.encode(itemElementName)

        url = '%s/itemElements/add/%s/%s' % (self.getContextRoot(), itemElementName, parentItemId)

        url = self.__appendOptionalItemElementParametersToUrl(url, containedItemId, description, ownerUserId,
                                                              ownerGroupId, isRequired, isGroupWriteable)

        responseData = self.sendSessionRequest(url=url, method='POST')
        return ItemElement(responseData)

    def updateItemElement(self, itemElementId, containedItemId=-1, isRequired=-1,
                          name=None, description=None, ownerUserId=None, ownerGroupId=None, isGroupWriteable=None):
        if itemElementId is not None:
            itemElementId = str(itemElementId)

        if itemElementId is None or not len(itemElementId):
            raise InvalidRequest("itemElementId must be provided")

        url = '%s/itemElements/update/%s' % (self.getContextRoot(), itemElementId)

        if name is not None:
            name = Encoder.encode(name)
            url = self._appendUrlParameter(url, "name", name)

        url = self.__appendOptionalItemElementParametersToUrl(url, containedItemId, description, ownerUserId,
                                                              ownerGroupId, isRequired, isGroupWriteable)

        responseData = self.sendSessionRequest(url=url, method='PUT')
        return ItemElement(responseData)

    def getParentItems(self, itemId):
        if itemId is not None:
            itemId = str(itemId)

        url = '%s/items/%s/parentItems' % (self.getContextRoot(), itemId)

        responseData = self.sendRequest(url=url, method="GET")
        return self.toCdbObjectList(responseData, Item)

    def getItemElementsForItem(self, itemId):
        if itemId is not None:
            itemId = str(itemId)

        url = '%s/items/%s/elementsByItemId' % (self.getContextRoot(), itemId)

        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, ItemElement)

    def getAvailableInventoryItemStatuses(self):
        url = "%s/items/domain/inventory/availableStatuses" %(self.getContextRoot())

        responseData = self.sendRequest(url, method="GET")
        return self.toCdbObjectList(responseData, AllowedPropertyValue)

    def getInventoryItemStatus(self, itemId):
        if itemId is None:
            raise InvalidRequest("Item id must be provided")

        itemId = str(itemId)
        url = '%s/items/domain/inventory/status/%s' % (self.getContextRoot(), itemId)

        response = self.sendRequest(url, method="GET")
        return PropertyValue(response)

    def updateInventoryItemStatus(self, itemId, status):
        if itemId is None:
            raise InvalidRequest("Item id must be provided")
        if status is None:
            raise InvalidRequest("Status must be provided")

        itemId = str(itemId)

        status = Encoder.encode(status)

        url = "%s/items/domain/inventory/status/%s/%s" % (self.getContextRoot(), itemId, status)

        response = self.sendSessionRequest(url, method="POST")

        return PropertyValue(response)

    def __appendOptionalItemElementParametersToUrl(self, url, containedItemId = -1, description=None,
                       ownerUserId=None, ownerGroupId=None, isRequired=-1, isGroupWriteable=None ):

        if containedItemId != -1:
            containedItemId = str(containedItemId)
            url = self._appendUrlParameter(url, "containedItemId", containedItemId)

        if description is not None:
            description = Encoder.encode(description)
            url = self._appendUrlParameter(url, "description", description)

        if ownerUserId is not None:
            ownerUserId = str(ownerUserId)
            url = self._appendUrlParameter(url, "ownerUserId", ownerUserId)

        if ownerGroupId is not None:
            ownerGroupId = str(ownerGroupId)
            url = self._appendUrlParameter(url, "ownerGroupId", ownerGroupId)

        if isRequired != -1:
            isRequired = str(isRequired)
            url = self._appendUrlParameter(url, "isRequired", isRequired)

        if isGroupWriteable is not None:
            isGroupWriteable = str(isGroupWriteable)
            url = self._appendUrlParameter(url, "isGroupWriteable", isGroupWriteable)

        return url
