#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.utility.encoder import Encoder
from cdb.common.objects.domain import Domain
from cdb.common.objects.log import Log
from cdb.common.objects.item import Item
from cdb.common.objects.itemElement import ItemElement
from cdb.common.objects.propertyValue import PropertyValue
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
    
    def addPropertyValueToItemWithId(self, itemId, propertyTypeName):
        if itemId is not None:
            itemId = str(itemId)
        if itemId is None or not len(itemId):
            raise InvalidRequest("itemId must be provided")
        if propertyTypeName is None or not len(propertyTypeName):
            raise InvalidRequest("propertyTypeName must be provided")

        propertyTypeName = Encoder.encode(propertyTypeName)

        url = '%s/items/%s/addPropertyValue/%s' % (self.getContextRoot(), itemId, propertyTypeName)

        responseDict = self.sendSessionRequest(url=url, method='POST')

        return PropertyValue(responseDict)

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

    def getDomains(self):
        url = '%s/itemDomains' % self.getContextRoot()
        responseData =self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, Domain)

    def addItem(self, domainName, name, itemIdentifier1=None, itemIdentifier2=None, qrId=None, description=None,
                ownerUserId=None, ownerGroupId=None, isGroupWriteable=None):
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
            
        responseData = self.sendSessionRequest(url, method='POST')

        return Item(responseData)
