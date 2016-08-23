#!/usr/bin/env python

import os
import sys
import shutil
import subprocess
from getpass import getpass
from mergeUtils import typesOfMerge

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
    from cdb.common.exceptions.objectNotFound import ObjectNotFound
    from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
except:
    directory = os.path.dirname(__file__)
    fullPath = os.path.abspath(directory + "/../../../..")
    print >> sys.stderr, "Environment not loaded. Please run `source %s/setup.sh` before running this script." %(fullPath)
    exit(1)

class MergeUtility():

    def __init__(self, databaseName, typeOfMerge):
        self.typeOfMerge = typeOfMerge
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

        if typeOfMerge != typesOfMerge.finish:
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
        self.locationDomainHandlerId = None
        self.locationDomainId = None
        self.locationRelationshipTypeName = None
        self.designEntityTypeName = None

        self.doneAllowedEntity = False

        self.newItemDesignDict = {}

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
        dbDirectoryName = self.dbName
        if self.__isAddOnMode():
            dbDirectoryName += "-Addon"
        self.newSchemaPopulateScriptsPath = "%s/%s" % (self.newSchemaPopulateScriptsPath, dbDirectoryName)
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

    def __isAddOnMode(self):
        return self.typeOfMerge == typesOfMerge.finish

    def __isUniqueAttributeEntityInList(self, list, entity, uniqueAttribute):
        for listEntity in list:
            if entity.data[uniqueAttribute] == listEntity.data[uniqueAttribute]:
                print listEntity.data[uniqueAttribute] + " was found."
                return True


    def populateGroupTable(self, addOnGroups = False):
        if self.__isAddOnMode():
            addedGroups = self.usersDbApi.getUserGroups()

        for group in self.allGroups:
            skipGroup = False;
            if self.__isAddOnMode():
                skipGroup = self.__isUniqueAttributeEntityInList(addedGroups, group, 'name')

            if not skipGroup:
                args = self.argsGenerator.getNameDescriptionArgs(group)
                self.usersDbApi.addGroup(*args)

    def populateUserTable(self):
        if self.__isAddOnMode():
            addedUsers = self.usersDbApi.getUsers()
        for user in self.allUsers:
            skipUser = False
            if self.__isAddOnMode():
                skipUser = self.__isUniqueAttributeEntityInList(addedUsers, user, 'username')

            username = user.data['username']
            if not skipUser:
                userData = user.data
                firstName = userData['firstName']
                lastName = userData['lastName']
                middleName = userData['middleName']
                description = userData['description']
                email = userData['email']
                self.usersDbApi.addUser(username, firstName, lastName, middleName, email, description, '')
            for group in user['userGroupList']:
                try:
                    self.usersDbApi.addUserToGroup(username, group.data['name'])
                except Exception as ex:
                    print ex.message

    def populateSources(self):
        if self.__isAddOnMode():
            addedSources = self.itemDbApi.getSources()

        for source in self.allSources:
            skipSource = False
            if self.__isAddOnMode():
                skipSource = self.__isUniqueAttributeEntityInList(addedSources, source, 'name')

            if not skipSource:
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
        if self.__isAddOnMode():
            addedLogTopics = self.logDbApi.getLogTopics()

        for logTopic in self.allLogTopics:
            skipLogTopic = False
            if self.__isAddOnMode():
                skipLogTopic = self.__isUniqueAttributeEntityInList(addedLogTopics, logTopic, 'name')

            if not skipLogTopic:
                args = self.argsGenerator.getNameDescriptionArgs(logTopic)
                self.logDbApi.addLogTopic(*args)

    def populatePropertyTypeHandler(self):
        if self.__isAddOnMode():
            addedPropertyTypeHandlers = self.propertyDbApi.getPropertyTypeHandlers()

        for propertyTypeHandler in self.allPropertyTypeHandlers:
            skipPropertyTypeHandler = False
            if self.__isAddOnMode():
                skipPropertyTypeHandler = self.__isUniqueAttributeEntityInList(addedPropertyTypeHandlers, propertyTypeHandler, 'name')

            if not skipPropertyTypeHandler:
                args = self.argsGenerator.getNameDescriptionArgs(propertyTypeHandler)
                self.propertyDbApi.addPropertyTypeHandler(*args)

    def populatePropertyTypeCategories(self):
        if self.__isAddOnMode():
            addedPropertyTypeCategories = self.propertyDbApi.getPropertyTypeCategories()

        for propertyTypeCategory in self.allPropertyTypeCategories:
            skipPropertyTypeCategory = False
            if self.__isAddOnMode():
                skipPropertyTypeCategory = self.__isUniqueAttributeEntityInList(addedPropertyTypeCategories, propertyTypeCategory, 'name')

            if not skipPropertyTypeCategory:
                args = self.argsGenerator.getNameDescriptionArgs(propertyTypeCategory)
                self.propertyDbApi.addPropertyTypeCategory(*args)
            
    def populatePropertyType(self):
        if self.__isAddOnMode():
            addedPropertyTypes = self.propertyDbApi.getPropertyTypes()

        for propertyType in self.allPropertyTypes:
            skipPropertyType = False
            if self.__isAddOnMode():
                skipPropertyType = self.__isUniqueAttributeEntityInList(addedPropertyTypes, propertyType, 'name')

            if not skipPropertyType:
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
        # Load Domain
        locationName = 'Location'
        if self.__isAddOnMode():
            locationDomainHandler = self.itemDbApi.getDomainHandlerByName(locationName)
        else:
            locationDomainHandler = self.itemDbApi.addDomainHandler(locationName, 'Handler responsible for locations.')
        self.locationDomainHandlerName = locationDomainHandler.data['name']
        self.locationDomainHandlerId = locationDomainHandler.data['id']
        if self.__isAddOnMode():
            locationDomain = self.itemDbApi.getDomainByName(locationName)
        else:
            locationDomain = self.itemDbApi.addDomain(locationName, 'Location Domain', self.locationDomainHandlerName)
        self.locationDomainName = locationDomain.data['name']
        self.locationDomainId = locationDomain.data['id']

        # Add Location Types
        for locationType in self.allLocationTypes:
            skipLocationType = False
            if self.__isAddOnMode():
                try:
                    self.itemDbApi.getItemType(locationType.data['name'], self.locationDomainHandlerId)
                    skipLocationType = True
                except ObjectNotFound as ex:
                    print ex.message
                    skipLocationType = False

            if not skipLocationType:
                args = self.argsGenerator.getNameDescriptionArgs(locationType, self.locationDomainHandlerName)
                self.itemDbApi.addItemType(*args)


        # Add default relationship for locations
        if self.__isAddOnMode():
            relationshipTypeHandler = self.itemDbApi.getRelationshipTypeHandlerByName(locationName)
        else:
            relationshipTypeHandler = self.itemDbApi.addRelationshipTypeHandler(locationName, 'Handler for location type relationships')
        relationshipTypeHandlerName = relationshipTypeHandler.data['name']

        if self.__isAddOnMode():
            relationshipType = self.itemDbApi.getRelationshipTypeByName(locationName)
        else:
            relationshipType = self.itemDbApi.addRelationshipType(locationName, 'Location Relationship', relationshipTypeHandlerName)
        self.locationRelationshipTypeName = relationshipType.data['name']

        for location in self.allLocations:
            self.__addLocation(location)

        uniqueNameAssigment = 0
        lastParentId = -1
        for locationLink in self.allLocationLinks:
            parentItem = self.__findLocationItem(locationLink.data['parentLocation'])
            containedItem =  self.__findLocationItem(locationLink.data['childLocation'])

            parentId = parentItem.data['id']
            containedItemId = containedItem.data['id']

            if lastParentId == parentId:
                uniqueNameAssigment += 1
            else:
                lastParentId = parentId
                uniqueNameAssigment = 1

            skipLocationLink = False

            if self.__isAddOnMode():
                itemElements = self.itemDbApi.getItemElementsByItemId(parentId)
                for itemElement in itemElements:
                    if itemElement.data['contained_item_id'] == containedItemId:
                        skipLocationLink = True
                        break;
            if not skipLocationLink:
                self.itemDbApi.addItemElement(str(uniqueNameAssigment), parentId, containedItemId, False, None, 1, 1, 1, 0)
            else:
                print "Skipping adding item element with parent id %s and contained id %s" % (parentId, containedItemId)

    def __addLocation(self, location):
        skipLocation = False

        if self.__isAddOnMode():
            addedLocation = self.__findLocationItem(location)
            skipLocation = addedLocation is not None

        if not skipLocation:
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
        else:
            print "Skipping location: " + location.data['name']

    def __findLocationItem(self, location):
        locationName = location.data['name']
        try:
            return self.itemDbApi.getItem(self.locationDomainId, locationName, None, None, None)
        except ObjectNotFound as ex:
            print ex.message
            return None

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

        if (self.newItemDesignDict[designName]):
            item = self.newItemDesignDict[designName]
        else:
            item = self.itemDbApi.getItem(self.catalogDomainId, designName, None, None, None)

        return item.data['id']

    def createEntityTypes(self):
        self.__createCatalogDomainComponentDesignEntityTypes()

    def __createCatalogDomainComponentDesignEntityTypes(self):
        self.__createCatalogDomain()
        if self.designEntityTypeName is None:
            designName = 'Design'
            if self.__isAddOnMode():
                designEntityType = self.itemDbApi.getEntityTypeByName(designName)
            else:
                designEntityType = self.itemDbApi.addEntityType(designName, 'classification for design type objects')
            self.designEntityTypeName = designEntityType.data['name']
        if self.componentEntityTypeName is None:
            componentName = 'Component'
            if self.__isAddOnMode():
                componentEntityType = self.itemDbApi.getEntityTypeByName(componentName)
            else:
                componentEntityType = self.itemDbApi.addEntityType(componentName, 'classification for component type objects')
            self.componentEntityTypeName = componentEntityType.data['name']

        # Should only be populated on the start or full mode
        if not self.__isAddOnMode():
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
            catalogName = 'Catalog'
            if self.__isAddOnMode():
                catalogDomainHandler = self.itemDbApi.getDomainHandlerByName(catalogName)
            else:
                catalogDomainHandler = self.itemDbApi.addDomainHandler(catalogName, 'Handler responsible for items in a catalog type domain.')
            self.catalogDomainHandlerName = catalogDomainHandler.data['name']

    def __createCatalogDomain(self):
        if self.catalogDomainName is None:
            self.__createCatalogDomainHandler()
            catalogName = 'Catalog'
            if self.__isAddOnMode():
                catalogDomain = self.itemDbApi.getDomainByName(catalogName)
            else:
                catalogDomain = self.itemDbApi.addDomain(catalogName, 'Catalog Domain', self.catalogDomainHandlerName)
            self.catalogDomainName = catalogDomain.data['name']
            self.catalogDomainId = catalogDomain.data['id']

    def populateProjects(self, projects):
        if self.__isAddOnMode():
            addedProjects = self.itemDbApi.getItemProjects()

        for project in projects:
            skipProject = False
            if self.__isAddOnMode():
                for addedProject in addedProjects:
                    if addedProject.data['name'] == project:
                        skipProject = True
                        break

            if not skipProject:
                self.itemDbApi.addItemProject(project, '')

    def populateCatalogInventoryItems(self, populateTypesAndCategoriesAsNeeded, defaultItemProject):
        self.__populateItemsUsingComponents(populateTypesAndCategoriesAsNeeded, defaultItemProject)
        self.__populateItemsUsingDesigns(defaultItemProject)

    def __addItem(self, domainName, name, derivedFromItemId, itemIdentifier1, itemIdentifier2, entityTypeName, qrId, description, *entityInfoArgs ):
        try:
            return self.itemDbApi.addItem(domainName, name, derivedFromItemId, itemIdentifier1, itemIdentifier2, entityTypeName, qrId, description, *entityInfoArgs)
        except ObjectAlreadyExists as ex:
            if self.__isAddOnMode():
                # item identifier 2 is not used in cdb 2 schema
                print "Assigning duplicate identifier to current item."
                itemIdentifier2 = "Duplicate DB merge entry"
                return self.itemDbApi.addItem(domainName, name, derivedFromItemId, itemIdentifier1, itemIdentifier2, entityTypeName, qrId, description, *entityInfoArgs)
            else:
                raise ex

    def __populateItemsUsingDesigns(self, defaultItemProjectName):
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

            currentItem = self.__addItem(self.catalogDomainName, name, derivedFromItemId, itemIdentifier1, itemIdentifier2, self.designEntityTypeName, qrId, description, *entityInfoArgs)

            itemId = currentItem.data['id']

            self.itemDbApi.addItemItemProject(itemId, defaultItemProjectName)

            selfItemElement = self.itemDbApi.getSelfElementByItemId(itemId)
            selfItemElementId = selfItemElement.data['id']

            designProperties = designData['designProperties']
            self.__populateDomainEntityProperties(selfItemElementId, designProperties)

            designLogs = designData['designLogs']
            self.__populateDomainEntityLogsForItemElements(selfItemElementId, designLogs)

            self.newItemDesignDict[name] = currentItem

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

    def __populateItemsUsingComponents(self, populateTypesAndCategoriesAsNeeded, defaultItemProjectName):
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

            currentItem = self.__addItem(self.catalogDomainName, name, derivedFromItemId, itemIdentifier1, itemIdentifier2, self.componentEntityTypeName, qrId, description, *entityInfoArgs)

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
            self.itemDbApi.addItemItemProject(itemId, defaultItemProjectName)
        
            componentLogs = componentData['componentLogs']
            self.__populateDomainEntityLogsForItemElements(selfItemElementId, componentLogs)
        
            componentProperties = componentData['componentProperties']
            self.__populateDomainEntityProperties(selfItemElementId, componentProperties)

            componentInstances = componentData['componentInstance']
            self.__addComponentInstances(itemId, componentInstances, defaultItemProjectName)

    def __addComponentInstances(self, itemId, componentInstances, defaultItemProjectName):
        if self.instanceDomainName is None:
            inventoryName = 'Inventory'
            if self.__isAddOnMode():
                instanceDomainHandler = self.itemDbApi.getDomainHandlerByName(inventoryName)
            else:
                instanceDomainHandler = self.itemDbApi.addDomainHandler(inventoryName, 'Handler responsible for handling phyisical inventory of items.')
            instanceDomainHandlerName = instanceDomainHandler.data['name']
            if self.__isAddOnMode():
                instanceDomain = self.itemDbApi.getDomainByName(inventoryName)
            else:
                instanceDomain = self.itemDbApi.addDomain(inventoryName, 'Inventory Domain', instanceDomainHandlerName)

            self.instanceDomainName = instanceDomain.data['name']

        numberAssignment = 0
        for componentInstance in componentInstances:
            numberAssignment += 1
            componentInstance = componentInstance.toCdbObject()
            componentInstanceData = componentInstance.data
            componentInstanceId = componentInstanceData['id']
            print 'Moving Component insatance id %s' % componentInstanceId

            name = componentInstanceData['tag']
            itemIdentifier1 = componentInstanceData['serialNumber']
            itemIdentifier2 = None
            description = componentInstanceData['description']
            derivedFromItemId=itemId
            qrId = componentInstanceData['qrId']
            entityInfoData = componentInstanceData['entityInfo'].data
            entityInfoArgs = self.argsGenerator.getEntityInfoArgs(entityInfoData)

            # Need to assign an arbitrary tag to non-unique component instance
            if name is None or not name.strip():
                name = 'Unit: %s' % (numberAssignment)


            currentItem = self.itemDbApi.addItem(self.instanceDomainName, name, derivedFromItemId, itemIdentifier1, itemIdentifier2, self.instanceEntityTypeName, qrId, description, *entityInfoArgs)
            instanceItemId = currentItem.data['id']

            selfItemElement = self.itemDbApi.getSelfElementByItemId(instanceItemId)
            selfItemElementId = selfItemElement.data['id']

            componentInstaceLogs = componentInstanceData['componentInstanceLogs']

            self.itemDbApi.addItemItemProject(instanceItemId, defaultItemProjectName)

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