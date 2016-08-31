from cdb.common.db.api.itemDbApi import ItemDbApi
from cdb.common.db.api.userDbApi import UserDbApi
from cdb.common.db.api.propertyDbApi import PropertyDbApi
from cdb.common.db.api.logDbApi import LogDbApi

from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.exceptions.cdbException import CdbException


class SparePartsTask:

    SPARE_PARTS_INDICATOR_PROPERTY_TYPE_NAME = "Spare Part Indication"
    SPARE_PARTS_CONFIGURATION_PROPERTY_TYPE_NAME = "Spare Parts Configuration"
    SPARE_PARTS_CONFIGURATION_MIN_KEY = 'minQuantity'
    SPARE_PARTS_CONFIGURATION_EMAIL_KEY = 'email'
    SPARE_PARTS_CONFIGURATION_NOEMAIL_VALUE = 'None'
    SPARE_PARTS_WARNING_LOG_LEVEL = "Spares Warning"
    CDB_SYSTEM_ACCOUNT_USERNAME = 'cdb'
    CATALOG_DOMAIN_NAME = "Catalog"

    def __init__(self):
        self.itemDbApi = ItemDbApi()
        self.userDbApi = UserDbApi()
        self.propertyDbApi = PropertyDbApi()
        self.logDbApi = LogDbApi()

    def getPropertyMetadataDict(self, propertyValue):
        propertyMetadataList = propertyValue.data['propertyMetadata']
        propertyMetadataDict = {}
        for propertyMetadata in propertyMetadataList:
            key = propertyMetadata.metadata_key
            value = propertyMetadata.metadata_value
            propertyMetadataDict[key] = value
        return propertyMetadataDict

    def getOwnerUserEmail(self, itemId):
        selfItemElement = self.itemDbApi.getSelfElementByItemId(itemId)
        entityInfo = selfItemElement.data['entityInfo']
        ownerUserInfo = entityInfo['ownerUserInfo']
        return ownerUserInfo.data['email']

    def getSystemAccountUserId(self):
        cdbUser = self.userDbApi.getUserByUsername(self.CDB_SYSTEM_ACCOUNT_USERNAME)
        return cdbUser.data['id']

    def getSelfElementIdForItemId(self, itemId):
        selfItemElement = self.itemDbApi.getSelfElementByItemId(itemId)
        return selfItemElement.data['id']

    def addSparePartsWarningLogEntryToItem(self, itemElementId, message):
        systemAccountUserId = self.getSystemAccountUserId()
        self.itemDbApi.addItemElementLog(itemElementId, message, systemAccountUserId, None, None, None, None, self.SPARE_PARTS_WARNING_LOG_LEVEL)

    @staticmethod
    def generateSparesMessage(minSpares, currentSpares):
        if currentSpares == 1:
            sparesWord = "spare"
        else:
            sparesWord = "spares"
        return "Item has %s %s & requires %s" % (currentSpares, sparesWord, minSpares)

    def checkSpares(self):
        catalogItemsWithSparePartsConfiguration = self.itemDbApi.getItemsWithPropertyType(
                self.SPARE_PARTS_CONFIGURATION_PROPERTY_TYPE_NAME, itemDomainName=self.CATALOG_DOMAIN_NAME)

        for catalogItem in catalogItemsWithSparePartsConfiguration:
            catalogItemId = catalogItem.data['id']
            catalogSelfElementId = self.getSelfElementIdForItemId(catalogItemId)

            sparePartsConfigurationPropertyValue = self.propertyDbApi.getPropertyValueListForItemElementId(
                    catalogSelfElementId, propertyTypeName=self.SPARE_PARTS_CONFIGURATION_PROPERTY_TYPE_NAME)

            if sparePartsConfigurationPropertyValue:
                metadataDict = self.getPropertyMetadataDict(sparePartsConfigurationPropertyValue[0])
            else:
                raise ObjectNotFound("Could not find required property: %s"
                                     % self.SPARE_PARTS_CONFIGURATION_PROPERTY_TYPE_NAME)

            if self.SPARE_PARTS_CONFIGURATION_MIN_KEY in metadataDict:
                minSpares = int(metadataDict[self.SPARE_PARTS_CONFIGURATION_MIN_KEY])
            else:
                raise CdbException("required metadata %s not specified for spare part configuration"
                                   % self.SPARE_PARTS_CONFIGURATION_MIN_KEY)

            email = ''
            if metadataDict.has_key(self.SPARE_PARTS_CONFIGURATION_EMAIL_KEY):
                emailValue = metadataDict[self.SPARE_PARTS_CONFIGURATION_EMAIL_KEY]
                if emailValue == self.SPARE_PARTS_CONFIGURATION_NOEMAIL_VALUE:
                    email = None
                else:
                    email = emailValue
            else:
                # Use owner user email.
                email = self.getOwnerUserEmail(catalogItemId)

            if email is not None:
                email = 'djarosz@aps.anl.gov'

            sparePartsList = self.itemDbApi.getItemsWithPropertyType(self.SPARE_PARTS_INDICATOR_PROPERTY_TYPE_NAME,
                                                                     itemDerivedFromItemId=catalogItemId,
                                                                     propertyValueMatch='true')
            spares = sparePartsList.__len__()

            if minSpares > spares:
                validNotification = True
                sparesMessage = self.generateSparesMessage(minSpares, spares)

                spareLogEntries = self.logDbApi.getLogEntriesForItemElementId(
                        catalogSelfElementId, self.SPARE_PARTS_WARNING_LOG_LEVEL)
                if spareLogEntries:
                    lastSpareLogEntry = spareLogEntries[-1]
                    lastSparesLogMessage = lastSpareLogEntry.data['text']
                    if lastSparesLogMessage == sparesMessage:
                        validNotification = False

                if validNotification:
                    self.addSparePartsWarningLogEntryToItem(catalogSelfElementId, sparesMessage)

                    if email is not None:
                        print email


