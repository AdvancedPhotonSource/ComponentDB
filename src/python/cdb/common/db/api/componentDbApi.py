#!/usr/bin/env python

from cdb.common.exceptions.cdbException import CdbException
from cdb.common.db.api.cdbDbApi import CdbDbApi
from cdb.common.db.impl.componentHandler import ComponentHandler
from cdb.common.db.impl.componentInstanceHandler import ComponentInstanceHandler
from cdb.common.db.impl.componentPropertyHandler import ComponentPropertyHandler
from cdb.common.db.impl.componentInstancePropertyHandler import ComponentInstancePropertyHandler
from cdb.common.db.impl.componentTypeHandler import ComponentTypeHandler
from cdb.common.db.impl.componentTypeCategoryHandler import ComponentTypeCategoryHandler
from cdb.common.db.impl.propertyTypeHandler import PropertyTypeHandler
from cdb.common.db.impl.propertyValueHandler import PropertyValueHandler
from cdb.common.db.impl.userInfoHandler import UserInfoHandler
from cdb.common.db.impl.entityInfoHandler import EntityInfoHandler

class ComponentDbApi(CdbDbApi):

    def __init__(self):
        CdbDbApi.__init__(self)
        self.componentTypeCategoryHandler = ComponentTypeCategoryHandler()
        self.componentTypeHandler = ComponentTypeHandler()
        self.componentInstanceHandler = ComponentInstanceHandler()
        self.propertyTypeHandler = PropertyTypeHandler()
        self.propertyValueHandler = PropertyValueHandler()
        self.componentHandler = ComponentHandler()
        self.componentPropertyHandler = ComponentPropertyHandler()
        self.componentInstancePropertyHandler = ComponentInstancePropertyHandler()
        self.userInfoHandler = UserInfoHandler()
        self.entityInfoHandler = EntityInfoHandler()

    @CdbDbApi.executeQuery
    def getComponentTypeCategories(self, **kwargs):
        session = kwargs['session']
        dbComponentTypeCategories = self.componentTypeCategoryHandler.getComponentTypeCategories(session)
        return self.toCdbObjectList(dbComponentTypeCategories)

    @CdbDbApi.executeQuery
    def getComponentTypes(self, **kwargs):
        session = kwargs['session']
        dbComponentTypes = self.componentTypeHandler.getComponentTypes(session)
        return self.toCdbObjectList(dbComponentTypes)

    @CdbDbApi.executeQuery
    def getComponentTypeByName(self, name, **kwargs):
        session = kwargs['session']
        dbComponentTypes = self.componentTypeHandler.getComponentTypeByName(session,name)
        return dbComponentTypes.getCdbObject()

    @CdbDbApi.executeQuery
    def getComponents(self, **kwargs):
        session = kwargs['session']
        dbComponents = self.componentHandler.getComponents(session)
        return self.toCdbObjectList(dbComponents)

    @CdbDbApi.executeQuery
    def getComponentsByName(self, name, **kwargs):
        session = kwargs['session']
        dbComponents = self.componentHandler.getComponentsByName(session, name)
        return self.toCdbObjectList(dbComponents)

    @CdbDbApi.executeQuery
    def getComponentById(self, id, **kwargs):
        session = kwargs['session']
        dbComponent = self.componentHandler.getComponentById(session, id)
        return dbComponent.getCdbObject()

    @CdbDbApi.executeQuery
    def getComponentByName(self, name, **kwargs):
        session = kwargs['session']
        dbComponent = self.componentHandler.getComponentByName(session, name)
        return dbComponent.getCdbObject()

    @CdbDbApi.executeQuery
    def getComponentByModelNumber(self, modelNumber, **kwargs):
        session = kwargs['session']
        dbComponent = self.componentHandler.getComponentByModelNumber(session, modelNumber)
        return dbComponent.getCdbObject()

    @CdbDbApi.executeTransaction
    def addComponent(self, name, modelNumber, componentTypeId, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description, **kwargs):
        session = kwargs['session']
        dbComponent = self.componentHandler.addComponent(session, name, modelNumber, componentTypeId, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description)
        return dbComponent.getCdbObject()

    @CdbDbApi.executeQuery
    def getPropertyTypeByName(self, name, **kwargs):
        session = kwargs['session']
        dbPropertyType = self.propertyTypeHandler.getPropertyTypeByName(session, name)
        return dbPropertyType.getCdbObject()

    @CdbDbApi.executeTransaction
    def addComponentPropertyByTypeId(self, componentId, propertyTypeId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable, **kwargs):
        session = kwargs['session']
        dbComponent = self.componentHandler.findComponentById(session, componentId)
        # Make sure user can update component
        dbUserInfo = self.userInfoHandler.getUserInfoById(session, enteredByUserId)
        self.entityInfoHandler.checkEntityIsWriteable(dbComponent.entityInfo, dbUserInfo, self.adminGroupName)

        dbPropertyValue = self.propertyValueHandler.createPropertyValueByTypeId(session, propertyTypeId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable)
        dbComponentProperty = self.componentPropertyHandler.addComponentProperty(session, dbComponent, dbPropertyValue)
        return dbComponentProperty.getCdbObject()

    @CdbDbApi.executeTransaction
    def updateComponentPropertyByValueId(self, componentId, propertyValueId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable, **kwargs):
        session = kwargs['session']
        dbComponent = self.componentHandler.findComponentById(session, componentId)
        dbComponentProperty = self.componentPropertyHandler.findComponentProperty(session, componentId, propertyValueId)
        # Make sure user can update component
        dbUserInfo = self.userInfoHandler.getUserInfoById(session, enteredByUserId)
        self.entityInfoHandler.checkEntityIsWriteable(dbComponent.entityInfo, dbUserInfo, self.adminGroupName)

        dbPropertyValue = self.propertyValueHandler.updatePropertyValueById(session, propertyValueId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable)
        return dbPropertyValue.getCdbObject()

    @CdbDbApi.executeTransaction
    def addComponentInstancePropertyByTypeId(self, componentInstanceId, propertyTypeId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable, **kwargs):
        session = kwargs['session']
        dbComponentInstance = self.componentInstanceHandler.findComponentInstanceById(session, componentInstanceId)
        # Make sure user can update component instance
        dbUserInfo = self.userInfoHandler.getUserInfoById(session, enteredByUserId)
        self.entityInfoHandler.checkEntityIsWriteable(dbComponentInstance.entityInfo, dbUserInfo, self.adminGroupName)

        dbPropertyValue = self.propertyValueHandler.createPropertyValueByTypeId(session, propertyTypeId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable)
        dbComponentInstanceProperty = self.componentInstanceHandler.addComponentInstanceProperty(session, dbComponentInstance, dbPropertyValue)
        return dbComponentInstanceProperty.getCdbObject()

    @CdbDbApi.executeTransaction
    def updateComponentInstancePropertyByValueId(self, componentInstanceId, propertyValueId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable, **kwargs):
        session = kwargs['session']
        dbComponentInstance = self.componentInstanceHandler.findComponentInstanceById(session, componentInstanceId)
        dbComponentInstanceProperty = self.componentInstancePropertyHandler.findComponentInstanceProperty(session, componentInstanceId, propertyValueId)

        # Make sure user can update component instance
        dbUserInfo = self.userInfoHandler.getUserInfoById(session, enteredByUserId)
        self.entityInfoHandler.checkEntityIsWriteable(dbComponentInstance.entityInfo, dbUserInfo, self.adminGroupName)

        dbPropertyValue = self.propertyValueHandler.updatePropertyValueById(session, propertyValueId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable)
        return dbPropertyValue.getCdbObject()

#######################################################################
# Testing.
if __name__ == '__main__':
    api = ComponentDbApi()
    #componentTypeCategories = api.getComponentTypeCategories()
    #for componentTypeCategory in componentTypeCategories:
    #    print componentTypeCategory

    #componentTypes = api.getComponentTypes()
    #for componentType in componentTypes:
    #    print componentType

    components = api.getComponentsByName('3458A DVM')
    for component in components:
        print
        print "********************"
        print component
    #    print "TEXT"
    #    print component.getTextRep()
    #    print "DICT"
    #    print component.getDictRep()
    #    print "JSON"
    #    print component.getJsonRep()

    #print 'Getting component'
    #component = api.getComponentById(10)
    #print component.getDictRep()

    print 'Adding component'
    component = api.addComponent(name='3458A DVM', modelNumber='xy1', componentTypeId=8, createdByUserId=4, ownerUserId=4, ownerGroupId=3, isGroupWriteable=True, description='Test Component')
    print "Added Component"
    print component

    #component = api.getComponentById(10)
    #print component.getDictRep()
    #api.setAdminGroupName('CDB_ADMIN')
    #print 'ADD COMPONENT PROPERTY'
    #print api.addComponentPropertyByTypeId(componentId=10, propertyTypeId=2, tag='mytag', value='A', units=None, description=None, enteredByUserId=4, isDynamic=False, isUserWriteable=False)

    #print 'UPDATE COMPONENT PROPERTY'
    #print api.updateComponentPropertyByValueId(componentId=207, propertyValueId=212, tag='mytag', value='NIM', units=None, description=None, enteredByUserId=4, isDynamic=False, isUserWriteable=False)

    #print 'ADD COMPONENT INSTANCE PROPERTY'
    #print api.addComponentInstancePropertyByTypeId(componentInstanceId=50, propertyTypeId=2, tag='mytag', value='A', units=None, description=None, enteredByUserId=4, isDynamic=False, isUserWriteable=False)

    #print 'UPDATE COMPONENT INSTANCE PROPERTY'
    #print api.updateComponentInstancePropertyByValueId(componentInstanceId=50, propertyValueId=344, tag='mytag', value='B', units=None, description=None, enteredByUserId=4, isDynamic=False, isUserWriteable=False)

