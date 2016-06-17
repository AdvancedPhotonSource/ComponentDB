#!/usr/bin/env python

import os
import sys
import shutil
import subprocess
from getpass import getpass

try:
    from dbLegacy.api.componentDbApi import ComponentDbApi as LegacyComponentDbApi
    from dbLegacy.api.logDbApi import LogDbApi as LegacyLogDbApi
    from dbLegacy.api.designDbApi import DesignDbApi as LegacyDesignDbApi
    from dbLegacy.api.locationDbApi import LocationDbApi as LegacyLocationDbApi
    from dbLegacy.api.propertyDbApi import PropertyDbApi as LegacyPropertyDbApi
    from dbLegacy.api.userDbApi import UserDbApi as LegacyUserDbApi

    from mergeUtils.argsGenerator import ArgsGenerator

    from cdb.common.db.api.userDbApi import UserDbApi
    from cdb.common.db.api.itemDbApi import ItemDbApi
    from cdb.common.db.api.logDbApi import LogDbApi
    from cdb.common.db.api.propertyDbApi import PropertyDbApi
    from cdb.common.db.api.developerDbApi import DeveloperDbApi
    from cdb.common.utility.configurationManager import ConfigurationManager
except:
    directory = os.path.dirname(__file__)
    fullPath = os.path.abspath(directory + "/../../../..")
    print >> sys.stderr, "Environment not loaded. Please run `source %s/setup.sh` before running this script." %(fullPath)
    exit(1)

class MergeUtility():

    def __init__(self, databaseName, createTempDb=True):
        self.dbName = databaseName
        self.tempDbName = 'cdb_temporary_db'
        self.rootDbPassword = None

        self.__fetchEnviornmentVariables()

        self.cm = ConfigurationManager.getInstance()
        self.__loadLegacyDbConfiguration()

        self.legacyDbComponent = LegacyComponentDbApi()
        self.legacyDbDesign = LegacyDesignDbApi()
        self.legacyDbLog = LegacyLogDbApi()
        self.legacyDbProperty = LegacyPropertyDbApi()
        self.legacyDbLocation = LegacyLocationDbApi()
        self.legacyDbUsers = LegacyUserDbApi()

        self.__loadLegacyData()

        if createTempDb:
            self.createTempDb()

        # Load New DB Utils
        self.__createConfigFile()

        cdbWebServiceConfigFile = self.tempDbConfigPath
        self.cm.setConfigFile(cdbWebServiceConfigFile)

        self.argsGenerator = ArgsGenerator()
        self.usersDbApi = UserDbApi()
        self.itemDbApi = ItemDbApi()
        self.logDbApi = LogDbApi()
        self.propertyDbApi = PropertyDbApi()
        self.developerDbApi = DeveloperDbApi()

        self.componentEntityTypeName = None
        self.catalogDomainName = None
        self.catalogDomainId = None
        self.catalogDomainHandlerName = None
        self.instanceDomainName = None
        self.locationEntityTypeName = None
        self.instanceEntityTypeName = None
        self.locationDomainName = None
        self.locationDomainHandlerName = None
        self.locationDomainId = None
        self.locationRelationshipTypeName = None
        self.designEntityTypeName = None

        self.doneAllowedEntity = False

    def backupCurrentDb(self):
        print 'Backing up original database.'
        cmd = '%s/sbin/cdb_backup_db.sh %s %s' % (self.cdbRootDir, self.dbName, self.oldSchemaBackup)
        code = subprocess.call(cmd, shell=True)
        if code != 0:
            print 'Could not backup original database.'
            exit(code)

    def backupTempDb(self):
        print 'Backing up new database.'
        self.__getRootDbPassword()
        cmd = 'echo \'%s\' | %s/sbin/cdb_backup_db.sh %s %s' % (self.tempDbName, self.cdbRootDir, self.tempDbName, self.newSchemaPopulateScriptsPath)
        code = subprocess.call(cmd, shell=True)
        if code != 0:
            print 'Could not backup new database.'
            exit(code)

    def destroyTempDb(self):
        self.__getRootDbPassword()
        cmd = '%s %s %s' % (self.destroyDbScriptPath, self.tempDbName, self.rootDbPassword)
        code = subprocess.call(cmd, shell=True)
        if code != 0:
            print 'Could not destroy temporary database'
            exit(code)

        # Remove password file
        os.remove(self.tempDbPasswordFile)

    def printSummaryInfo(self):
        print '*******************************\n'
        print 'Backup path of old schema database: %s' % self.oldSchemaBackup
        print 'Backup path for the new schema database: %s' % self.newSchemaPopulateScriptsPath
        print '\n*******************************'

    def __getRootDbPassword(self):
        if self.rootDbPassword is None:
            self.rootDbPassword = getpass(prompt='DB root Password: ')


    def createTempDb(self):
        print "\n Creating NEW DB: %s" % self.tempDbName
        self.__getRootDbPassword()

        file = open(self.tempDbPasswordFile, 'w+')
        file.write(self.tempDbName)
        file.close()
        cmd = 'echo \'%s\' |%s/sbin/cdb_create_db.sh %s %s/db/sql/cdb 1' % (self.rootDbPassword, self.cdbRootDir, self.tempDbName, self.cdbRootDir)
        code = subprocess.call(cmd, shell=True)
        if code != 0:
            print 'Could not create temporary database'
            exit(code)
        print "\n DONE CREATING DB \n\n"

    def __loadLegacyDbConfiguration(self):
        cdbWebServiceConfigFile = '%s/etc/%s.cdb-web-service.conf' % (self.cdbInstallDir, self.dbName)
        print 'Loading db config file: %s' % cdbWebServiceConfigFile

        if os.path.isfile(cdbWebServiceConfigFile):
            self.cm.setConfigFile(cdbWebServiceConfigFile)
        else:
            print >> sys.stderr, "Configuration file does not exist: %s." %(cdbWebServiceConfigFile)
            exit(1)


    def __createConfigFile(self):
        configTemplate = "%s/etc/cdb-web-service.conf.template" % self.cdbRootDir
        self.tempDbConfigPath = "%s/tmp.cdb-web-service.conf" % self.scriptDataPath
        shutil.copyfile(configTemplate, self.tempDbConfigPath)

        file = open(self.tempDbConfigPath, 'r+w')
        fileData = file.read()
        fileData = fileData.replace('dbSchema=CDB_DB_NAME', 'dbSchema=%s' % self.tempDbName)
        fileData = fileData.replace('dbUser=CDB_DB_NAME', 'dbUser=%s' % self.tempDbName)
        fileData = fileData.replace('dbPasswordFile=CDB_INSTALL_DIR/etc/CDB_DB_NAME.db.passwd', 'dbPasswordFile=%s' % self.tempDbPasswordFile)
        file.write(fileData)

        file.close()

    def __fetchEnviornmentVariables(self):
        scriptDirectory = os.path.dirname(__file__)
        self.destroyDbScriptPath = os.path.abspath('%s/../sbin/cdb_destroy_db.sh' % scriptDirectory)


        self.cdbRootDir = os.environ.get('CDB_ROOT_DIR')
        self.cdbInstallDir = os.environ.get('CDB_INSTALL_DIR')
        self.cdbTmpDir = "%s/tmp" %self.cdbInstallDir
        if os.path.exists(self.cdbTmpDir) is False:
            os.mkdir(self.cdbTmpDir)
        self.scriptDataPath = "%s/dbMerge" % self.cdbTmpDir
        if os.path.exists(self.scriptDataPath) is False:
            os.mkdir(self.scriptDataPath)
        self.oldSchemaBackup = "%s/legacyDbBackUp" % self.scriptDataPath
        if os.path.exists(self.oldSchemaBackup) is False:
            os.mkdir(self.oldSchemaBackup)
        self.oldSchemaBackup = "%s/%s" % (self.oldSchemaBackup, self.dbName)
        if os.path.exists(self.oldSchemaBackup) is False:
            os.mkdir(self.oldSchemaBackup)
        self.newSchemaPopulateScriptsPath = "%s/newPopulateScripts" % self.scriptDataPath
        if os.path.exists(self.newSchemaPopulateScriptsPath) is False:
            os.mkdir(self.newSchemaPopulateScriptsPath)
        self.newSchemaPopulateScriptsPath = "%s/%s" % (self.newSchemaPopulateScriptsPath, self.dbName)
        if os.path.exists(self.newSchemaPopulateScriptsPath) is False:
            os.mkdir(self.newSchemaPopulateScriptsPath)

        self.tempDbPasswordFile = '%s/etc/%s.db.passwd' % (self.cdbInstallDir, self.tempDbName)


    def __loadLegacyData(self):
        self.allGroups = self.legacyDbUsers.getUserGroups()
        self.allUsers = self.legacyDbUsers.getUsers()
        self.allComponents = self.legacyDbComponent.getComponents()
        self.allDesigns = self.legacyDbDesign.getDesigns()
        self.allSources = self.legacyDbComponent.getSources()
        self.allCategories = self.legacyDbComponent.getComponentTypeCategories()
        self.allTypes = self.legacyDbComponent.getComponentTypes()
        self.allLogTopics = self.legacyDbLog.getLogTopics()
        self.allPropertyTypeHandlers = self.legacyDbProperty.getPropertyTypeHandlers()
        self.allPropertyTypeCategories = self.legacyDbProperty.getPropertyTypeCategories()
        self.allPropertyTypes = self.legacyDbProperty.getPropertyTypes()
        self.allLocationTypes = self.legacyDbLocation.getLocationTypes()
        self.allLocations = self.legacyDbLocation.getLocations()
        self.allLocationLinks = self.legacyDbLocation.getLocationLinks()

    def populateGroupTable(self):
        for group in self.allGroups:
            args = self.argsGenerator.getNameDescriptionArgs(group)
            self.usersDbApi.addGroup(*args)

    def populateUserTable(self):
        for user in self.allUsers:
            userData = user.data
            firstName = userData['firstName']
            lastName = userData['lastName']
            middleName = userData['middleName']
            description = userData['description']
            email = userData['email']
            username = userData['username']
            self.usersDbApi.addUser(username, firstName, lastName, middleName, email, description, '')
            for group in user['userGroupList']:
                self.usersDbApi.addUserToGroup(username, group.data['name'])

    def populateSources(self):
        for source in self.allSources:
            sourceData = source.data
            sourceName = sourceData['name']
            description = sourceData['description']
            contactInfo = sourceData['contactInfo']
            url = source['url']
            self.itemDbApi.addSource(sourceName, description, contactInfo, url)

    def populateCategories(self):
        self.__createCatalogDomain()
        for category in self.allCategories:
            args = self.argsGenerator.getNameDescriptionArgs(category, self.catalogDomainHandlerName)
            self.itemDbApi.addItemCategory(*args)

    def populateTypes(self):
        self.__createCatalogDomain()
        for type in self.allTypes:
            args = self.argsGenerator.getNameDescriptionArgs(type, self.catalogDomainHandlerName)
            self.itemDbApi.addItemType(*args)

    def populateLogTopics(self):
        for logTopic in self.allLogTopics:
            args = self.argsGenerator.getNameDescriptionArgs(logTopic)
            self.logDbApi.addLogTopic(*args)

    def populatePropertyTypeHandler(self):
        for propertyTypeHandler in self.allPropertyTypeHandlers:
            args = self.argsGenerator.getNameDescriptionArgs(propertyTypeHandler)
            self.propertyDbApi.addPropertyTypeHandler(*args)

    def populatePropertyTypeCategories(self):
        for propertyTypeCategory in self.allPropertyTypeCategories:
            args = self.argsGenerator.getNameDescriptionArgs(propertyTypeCategory)
            self.propertyDbApi.addPropertyTypeCategory(*args)
            
    def populatePropertyType(self):
        for propertyType in self.allPropertyTypes:
            propertyTypeData = propertyType.data
            propertyTypeName = propertyTypeData['name']
            description = propertyTypeData['description']
        
        
            propertyTypeCategory = propertyTypeData['propertyTypeCategory']
            propertyTypeCategoryName = None
            if propertyTypeCategory:
                propertyTypeCategoryName = propertyTypeCategory.data['name']
        
            propertyTypeHandler = propertyTypeData['propertyTypeHandler']
            propertyTypeHandlerName = None
            if propertyTypeHandler:
                propertyTypeHandlerName = propertyTypeHandler.data['name']
            defaultValue = propertyTypeData['defaultValue']
            defaultUnits = propertyTypeData['defaultUnits']
            isUserWriteable = False
            isDynamic = False
            isInternal = False
            isActive = True
        
            self.propertyDbApi.addPropertyType(propertyTypeName, description, propertyTypeCategoryName, propertyTypeHandlerName, defaultValue, defaultUnits, isUserWriteable, isDynamic, isInternal, isActive)

            allowedPropertyValues = propertyTypeData['allowedPropertyValueList']
            for allowedPropertyValue in allowedPropertyValues:
                value = allowedPropertyValue.value
                units = allowedPropertyValue.units
                description = allowedPropertyValue.description
                sortOrder = allowedPropertyValue.sort_order
        
                self.propertyDbApi.addAllowedPropertyValue(propertyTypeName, value, units, description, sortOrder)

    def populateItemsUsingLocations(self):
        # Create Domain
        locationDomainHandler = self.itemDbApi.addDomainHandler('Location', 'Handler responsible for locations.')
        self.locationDomainHandlerName = locationDomainHandler.data['name']
        locationDomain = self.itemDbApi.addDomain('Location', 'Location Domain', self.locationDomainHandlerName)
        self.locationDomainName = locationDomain.data['name']
        self.locationDomainId = locationDomain.data['id']

        # Add Location Types
        for locationType in self.allLocationTypes:
            args = self.argsGenerator.getNameDescriptionArgs(locationType, self.locationDomainHandlerName)
            self.itemDbApi.addItemType(*args)

        # Add default relationship for locations
        relationshipTypeHandler = self.itemDbApi.addRelationshipTypeHandler('Location', 'Handler for location type relationships')
        relationshipTypeHandlerName = relationshipTypeHandler.data['name']

        relationshipType = self.itemDbApi.addRelationshipType('Location', 'Location Relationship', relationshipTypeHandlerName)
        self.locationRelationshipTypeName = relationshipType.data['name']

        for location in self.allLocations:
            self.__addLocation(location)

        uniqueNameAssigment = 0
        lastParentId = -1
        for locationLink in self.allLocationLinks:
            parentItem = self.__findLocationItem(locationLink.data['parentLocation'])
            childItem =  self.__findLocationItem(locationLink.data['childLocation'])

            parentId = parentItem.data['id']
            childId = childItem.data['id']

            if lastParentId == parentId:
                uniqueNameAssigment += 1
            else:
                lastParentId = parentId
                uniqueNameAssigment = 1

            self.itemDbApi.addItemElement(str(uniqueNameAssigment), parentId, childId, False, None, 1, 1, 1, 0)

    def __addLocation(self, location):
        locationData = location.data
        name = locationData['name']
        itemIdentifier1 = None
        itemIdentifier2 = None
        description = locationData['description']
        derivedFromItemId=None
        qrId = None

        dbLocationItem = self.itemDbApi.addItem(self.locationDomainName, name, derivedFromItemId, itemIdentifier1, itemIdentifier2, self.locationEntityTypeName, qrId, description, 1, 1, 1, 0)
        locationTypeName = locationData['locationType'].data['name']
        self.itemDbApi.addItemItemType(dbLocationItem.data['id'], locationTypeName)

        return dbLocationItem

    def __findLocationItem(self, location):
        locationName = location.data['name']
        return self.itemDbApi.getItem(self.locationDomainId, locationName, None, None, None)

    def __findComponentItemIdById(self, componentId):
        component = self.legacyDbComponent.getComponentById(componentId)
        componentName = component.data['name']
        itemIdentifier1 = component.data['modelNumber']

        item = self.itemDbApi.getItem(self.catalogDomainId, componentName, itemIdentifier1, None, None)

        return item.data['id']

    def __findDesignItemId(self, designId, design=None):
        if design is None:
            design = self.legacyDbDesign.getDesignById(designId)
        designName = design.data['name']

        item = self.itemDbApi.getItem(self.catalogDomainId, designName, None, None, None)

        return item.data['id']

    def createEntityTypes(self):
        self.__createCatalogDomainComponentDesignEntityTypes()

    def __createCatalogDomainComponentDesignEntityTypes(self):
        self.__createCatalogDomain()
        if self.designEntityTypeName is None:
            designEntityType = self.itemDbApi.addEntityType('Design', 'classification for design type objects')
            self.designEntityTypeName = designEntityType.data['name']
        if self.componentEntityTypeName is None:
            componentEntityType = self.itemDbApi.addEntityType('Component', 'classification for component type objects')
            self.componentEntityTypeName = componentEntityType.data['name']


        if self.doneAllowedEntity is False:
            # Add allowed child entity types
            self.itemDbApi.addAllowedChildEntityType(self.componentEntityTypeName, self.designEntityTypeName)
            self.itemDbApi.addAllowedChildEntityType(self.componentEntityTypeName, self.componentEntityTypeName)

            self.itemDbApi.addAllowedChildEntityType(self.designEntityTypeName, self.componentEntityTypeName)
            self.itemDbApi.addAllowedChildEntityType(self.designEntityTypeName, self.designEntityTypeName)

            # Add allowed entity type domain handler
            self.itemDbApi.addAllowedDomainHandlerEntityType(self.catalogDomainHandlerName, self.designEntityTypeName)
            self.itemDbApi.addAllowedDomainHandlerEntityType(self.catalogDomainHandlerName, self.componentEntityTypeName)

            self.doneAllowedEntity = True

    def __createCatalogDomainHandler(self):
        if self.catalogDomainHandlerName is None:
            catalogDomainHandler = self.itemDbApi.addDomainHandler('Catalog', 'Handler responsible for items in a catalog type domain.')
            self.catalogDomainHandlerName = catalogDomainHandler.data['name']

    def __createCatalogDomain(self):
        if self.catalogDomainName is None:
            self.__createCatalogDomainHandler()
            catalogDomain = self.itemDbApi.addDomain('Catalog', 'Catalog Domain', self.catalogDomainHandlerName)
            self.catalogDomainName = catalogDomain.data['name']
            self.catalogDomainId = catalogDomain.data['id']

    def populateCatalogInventoryItems(self, populateTypesAndCategoriesAsNeeded):
        self.__populateItemsUsingComponents(populateTypesAndCategoriesAsNeeded)
        self.__populateItemsUsingDesigns()

    def __populateItemsUsingDesigns(self):
        self.__createCatalogDomainComponentDesignEntityTypes()

        for design in self.allDesigns:
            designData = design.data

            name = designData['name']
            itemIdentifier1 = None
            itemIdentifier2 = None
            description = designData['description']
            derivedFromItemId = None
            qrId = None
            entityInfoData = designData['entityInfo'].data
            entityInfoArgs = self.argsGenerator.getEntityInfoArgs(entityInfoData)

            # A model number must be assigned if a component already exists with same attributes.
            try:
                self.itemDbApi.getItem(self.catalogDomainId, name, itemIdentifier1, itemIdentifier2, None)
                itemIdentifier1 = "Auto Assigned Design Model"
            except:
                pass

            currentItem = self.itemDbApi.addItem(self.catalogDomainName, name, derivedFromItemId, itemIdentifier1, itemIdentifier2, self.designEntityTypeName, qrId, description, *entityInfoArgs)
            itemId = currentItem.data['id']

            selfItemElement = self.itemDbApi.getSelfElementByItemId(itemId)
            selfItemElementId = selfItemElement.data['id']

            designProperties = designData['designProperties']
            self.__populateDomainEntityProperties(selfItemElementId, designProperties)

            designLogs = designData['designLogs']
            self.__populateDomainEntityLogsForItemElements(selfItemElementId, designLogs)

        self.__populateItemElementsWithDesignElements()

    def __populateItemElementsWithDesignElements(self):
        for design in self.allDesigns:
            itemId = self.__findDesignItemId(None, design)

            designId = design.data['id']
            designElementList = self.legacyDbDesign.getDesignElementsByParentId(designId)
            for designElement in designElementList:
                designElementData = designElement.data

                childDesignId = designElementData['childDesignId']
                componentId = designElementData['componentId']

                containedItemId = None

                if childDesignId:
                    containedItemId = self.__findDesignItemId(childDesignId)
                elif componentId:
                    containedItemId = self.__findComponentItemIdById(componentId)

                sortOrder = designElementData['sortOrder']
                description = designElementData['description']
                name = designElementData['name']
                entityInfoData = designElementData['entityInfo'].data
                entityInfoArgs = self.argsGenerator.getEntityInfoArgs(entityInfoData)

                itemElement = self.itemDbApi.addItemElement(name, itemId, containedItemId, True, description, *entityInfoArgs)
                itemElementId = itemElement.data['id']

                designElementProperties = designElementData['designElementProperties']
                self.__populateDomainEntityProperties(itemElementId, designElementProperties)

                designElementLogs = designElementData['designElementLogs']
                self.__populateDomainEntityLogsForItemElements(itemElementId, designElementLogs)

    def __populateItemsUsingComponents(self, populateTypesAndCategoriesAsNeeded=False):
        self.__createCatalogDomainComponentDesignEntityTypes()
        
        for component in self.allComponents:
            print 'Moving Component id %s' % component.data['id']
            componentData = component.data

            name = componentData['name']
            itemIdentifier1 = componentData['modelNumber']
            itemIdentifier2 = None
            description = componentData['description']
            derivedFromItemId=None
            qrId = None
            entityInfoData = componentData['entityInfo'].data
            entityInfoArgs = self.argsGenerator.getEntityInfoArgs(entityInfoData)


            currentItem = self.itemDbApi.addItem(self.catalogDomainName, name, derivedFromItemId, itemIdentifier1, itemIdentifier2, self.componentEntityTypeName, qrId, description, *entityInfoArgs)
            itemId = currentItem.data['id']

            selfItemElement = self.itemDbApi.getSelfElementByItemId(itemId)
            selfItemElementId = selfItemElement.data['id']
        
            componentSources = componentData['componentSources']
            if componentSources:
                for componentSource in componentSources:
                    sourceName = componentSource.source.name
                    partNumber = componentSource.part_number
                    cost = componentSource.cost
                    description = componentSource.description
                    isVendor = componentSource.is_vendor
                    isManufacturer = componentSource.is_manufacturer
                    contactInfo = componentSource.contact_info
                    url = componentSource.url
                    self.itemDbApi.addItemSource(itemId, sourceName, partNumber, cost, description, isVendor, isManufacturer, contactInfo, url)
        
            componentType = componentData['componentType']
            componentTypeCategory = componentType.data['componentTypeCategory']
        
            categoryName = componentTypeCategory.data['name']
            typeName = componentType.data['name']

            if populateTypesAndCategoriesAsNeeded:
                categoryDescription = componentTypeCategory.data['description']
                typeDescription = componentType.data['description']
                try:
                    self.itemDbApi.addItemType(typeName, typeDescription, self.catalogDomainHandlerName)
                except:
                    pass
                try:
                    self.itemDbApi.addItemCategory(categoryName, categoryDescription, self.catalogDomainHandlerName)
                except:
                    pass
        
            self.itemDbApi.addItemItemCategory(itemId, categoryName)
            self.itemDbApi.addItemItemType(itemId, typeName)
        
            componentLogs = componentData['componentLogs']
            self.__populateDomainEntityLogsForItemElements(selfItemElementId, componentLogs)
        
            componentProperties = componentData['componentProperties']
            self.__populateDomainEntityProperties(selfItemElementId, componentProperties)

            componentInstances = componentData['componentInstance']
            self.__addComponentInstances(itemId, componentInstances)

    def __addComponentInstances(self, itemId, componentInstances):
        if self.instanceDomainName is None:
            instanceDomainHandler = self.itemDbApi.addDomainHandler('Inventory', 'Handler responsible for handling phyisical inventory of items.')
            instanceDomainHandlerName = instanceDomainHandler.data['name']
            instanceDomain = self.itemDbApi.addDomain('Inventory', 'Inventory Domain', instanceDomainHandlerName)

            self.instanceDomainName = instanceDomain.data['name']

        numberAssignment = 0
        for componentInstance in componentInstances:
            numberAssignment += 1
            componentInstance = componentInstance.toCdbObject()
            componentInstanceData = componentInstance.data
            componentInstanceId = componentInstanceData['id']
            print 'Moving Component insatance id %s' % componentInstanceId

            name = None
            itemIdentifier1 = componentInstanceData['serialNumber']
            itemIdentifier2 = componentInstanceData['tag']
            description = componentInstanceData['description']
            derivedFromItemId=itemId
            qrId = componentInstanceData['qrId']
            entityInfoData = componentInstanceData['entityInfo'].data
            entityInfoArgs = self.argsGenerator.getEntityInfoArgs(entityInfoData)

            if itemIdentifier1 == "":
                itemIdentifier1 = None
            if itemIdentifier2 == "":
                itemIdentifier2 = None

            # Need to assign an arbitrary tag to non-unique component instance
            if itemIdentifier1 is None and itemIdentifier2 is None:
                itemIdentifier2 = 'Auto Tag %s' % (numberAssignment)


            currentItem = self.itemDbApi.addItem(self.instanceDomainName, name, derivedFromItemId, itemIdentifier1, itemIdentifier2, self.instanceEntityTypeName, qrId, description, *entityInfoArgs)
            instanceItemId = currentItem.data['id']

            selfItemElement = self.itemDbApi.getSelfElementByItemId(instanceItemId)
            selfItemElementId = selfItemElement.data['id']

            componentInstaceLogs = componentInstanceData['componentInstanceLogs']
            self.__populateDomainEntityLogsForItemElements(selfItemElementId, componentInstaceLogs)

            componentInstanceProperties = componentInstanceData['componentInstanceProperties']
            self.__populateDomainEntityProperties(selfItemElementId, componentInstanceProperties)

            # Add location details
            location = componentInstanceData['location']
            locationDetails = componentInstanceData['locationDetails']
            itemElementRelationship = None

            if location is not None:
                locationItem = self.__findLocationItem(location)
                locationItemId = locationItem.data['id']


                locationSelfItemElement = self.itemDbApi.getSelfElementByItemId(locationItemId)
                locationSelfItemElementId = locationSelfItemElement.data['id']

                itemElementRelationship = self.itemDbApi.addItemElementRelationship(selfItemElementId, locationSelfItemElementId, None, None, None,
                                                          self.locationRelationshipTypeName, locationDetails, None, None, None)
            elif locationDetails is not None:
                itemElementRelationship = self.itemDbApi.addItemElementRelationship(selfItemElementId, None, None, None, None,
                                                          self.locationRelationshipTypeName, locationDetails, None, None, None)

            self.__addComponentInstanceLocationHistory(selfItemElementId, itemElementRelationship, componentInstance)

    def __addComponentInstanceLocationHistory(self, selfItemElementId, itemElementRelationship, legacyComponentInstance):
        componentInstanceLocationHistory = legacyComponentInstance['componentInstanceLocationHistory']
        if componentInstanceLocationHistory.__len__() != 0:
            if itemElementRelationship is None:
                itemElementRelationship = self.itemDbApi.addItemElementRelationship(selfItemElementId, None, None, None, None,
                                                          self.locationRelationshipTypeName, None, None, None, None)
                pass
            for locationHistory in componentInstanceLocationHistory:
                locationHistoryData = locationHistory.toCdbObject().data
                itemElementRelationshipId = itemElementRelationship.data['id']

                locationItem = self.__findLocationItem(locationHistoryData['location'])
                locationItemId = locationItem.data['id']
                locationSelfItemElement = self.itemDbApi.getSelfElementByItemId(locationItemId)
                locationSelfItemElementId = locationSelfItemElement.data['id']

                locationDetails = locationHistoryData['locationDetails']

                enteredByUserName = locationHistoryData['userInfo'].data['username']
                enteredByUserId = self.argsGenerator.getUserId(enteredByUserName)
                enteredOnDateTime = locationHistoryData['enteredOnDateTime']

                self.developerDbApi.addItemElementRelationshipHistory(itemElementRelationshipId, selfItemElementId, locationSelfItemElementId, None, None, None,locationDetails,
                                                                      None,None,None, enteredByUserId, enteredOnDateTime)


    def __populateDomainEntityLogsForItemElements(self, itemElementId, domainEntityLogs):
        for domainEntityLog in domainEntityLogs:
            logArgs = self.argsGenerator.getLogArgs(domainEntityLog)
            # Add New log
            newLog = self.itemDbApi.addItemElementLog(itemElementId, *logArgs)
            self.__populateLogAttachments(newLog, domainEntityLog)

    def __populateLogAttachments(self, newLog, legacyLog):
        log = legacyLog.log
         # Add Log Attachments if any
        newLogId = newLog.data['log'].data['id']
        logAttachments = log.logAttachments
        for logAttachment in logAttachments:
            attachment = logAttachment.attachment
            attachmentName = attachment.name
            attachmentDescription = attachment.description
            attachmentTag = attachment.tag

            self.logDbApi.addLogAttachment(newLogId, attachmentName, attachmentTag, attachmentDescription)

    def __populateDomainEntityProperties(self, itemElementId, domainEntityProperties):
        for domainEntityProperty in domainEntityProperties:
            propertyValue = domainEntityProperty.propertyValue

            propertyTypeName = propertyValue.propertyType.name
            tag = propertyValue.tag
            value = propertyValue.value
            units = propertyValue.units
            description = propertyValue.description
            enteredOnDateTime = propertyValue.entered_on_date_time
            enteredByUserName = propertyValue.enteredByUserInfo.username
            enteredByUserId = self.argsGenerator.getUserId(enteredByUserName)
            isUserWriteable = propertyValue.is_user_writeable
            isDynamic = propertyValue.is_dynamic
            displayValue = propertyValue.display_value
            targetValue = propertyValue.target_value

            newItemElementProperty = self.itemDbApi.addItemElementProperty(itemElementId, propertyTypeName, tag, value, units, description, enteredByUserId, isUserWriteable, isDynamic, displayValue, targetValue, enteredOnDateTime)

            propertyValueHistoryList = propertyValue.propertyValueHistory
            for propertyValueHistory in propertyValueHistoryList:

                propertyValueId = newItemElementProperty.data['propertyValue'].data['id']
                tag = propertyValueHistory.tag
                value = propertyValueHistory.value
                units = propertyValueHistory.units
                description = propertyValueHistory.description
                enteredOnDateTime = propertyValueHistory.entered_on_date_time
                enteredByUserName = propertyValueHistory.enteredByUserInfo.username
                enteredByUserId = self.argsGenerator.getUserId(enteredByUserName)
                displayValue = propertyValueHistory.display_value
                targetValue = propertyValueHistory.target_value

                self.developerDbApi.addPropertyValueHistory(propertyValueId, tag, value, units, description, enteredByUserId, enteredOnDateTime, displayValue, targetValue)

                print propertyValueHistory