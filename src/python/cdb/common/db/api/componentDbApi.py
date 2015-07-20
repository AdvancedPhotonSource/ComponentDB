#!/usr/bin/env python

from cdb.common.exceptions.cdbException import CdbException
from cdb.common.db.api.cdbDbApi import CdbDbApi
from cdb.common.db.impl.componentHandler import ComponentHandler
from cdb.common.db.impl.componentInstanceHandler import ComponentInstanceHandler
from cdb.common.db.impl.componentPropertyHandler import ComponentPropertyHandler
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
    def getComponents(self, **kwargs):
        session = kwargs['session']
        dbComponents = self.componentHandler.getComponents(session)
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

    @CdbDbApi.executeTransaction
    def addComponent(self, name, componentTypeId, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description, **kwargs):
        session = kwargs['session']
        dbComponent = self.componentHandler.addComponent(session, name, componentTypeId, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description)
        return dbComponent.getCdbObject()

    @CdbDbApi.executeTransaction
    def addComponentPropertyByTypeId(self, componentId, propertyTypeId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable, **kwargs):
        session = kwargs['session']
        dbComponent = self.componentHandler.findComponentById(session, componentId)
        dbUserInfo = self.userInfoHandler.getUserInfoById(session, enteredByUserId)
        # Make sure user can update component
        self.entityInfoHandler.checkEntityIsWriteable(dbComponent.entityInfo, dbUserInfo, self.adminGroupName)

        dbPropertyValue = self.propertyValueHandler.createPropertyValueByTypeId(session, propertyTypeId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable)
        dbComponentProperty = self.componentPropertyHandler.addComponentProperty(session, dbComponent, dbPropertyValue)
        return dbComponentProperty.getCdbObject()

    @CdbDbApi.executeTransaction
    def updateComponentPropertyByValueId(self, componentId, propertyValueId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable, **kwargs):
        session = kwargs['session']
        dbComponent = self.componentHandler.findComponentById(session, componentId)
        dbComponentProperty = self.componentPropertyHandler.findComponentProperty(session, componentId, propertyValueId)
        dbPropertyValue = self.propertyValueHandler.updatedPropertyValueByTypeId(session, propertyTypeId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable)
        dbComponentProperty = self.componentPropertyHandler.addComponentProperty(session, dbComponent, dbPropertyValue)
        return dbComponentProperty.getCdbObject()

    @CdbDbApi.executeTransaction
    def addComponentInstancePropertyByTypeId(self, componentInstanceId, propertyTypeId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable, **kwargs):
        session = kwargs['session']
        dbComponentInstance = self.componentInstanceHandler.findComponentInstanceById(session, componentInstanceId)
        dbPropertyValue = self.propertyValueHandler.createPropertyValueByTypeId(session, propertyTypeId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable)
        dbComponentInstanceProperty = self.componentInstanceHandler.addComponentInstanceProperty(session, dbComponentInstance, dbPropertyValue)
        return dbComponentInstanceProperty.getCdbObject()

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

    #components = api.getComponents()
    #for component in components:
    #    print
    #    print "********************"
    #    print component
    #    print "TEXT"
    #    print component.getTextRep()
    #    print "DICT"
    #    print component.getDictRep()
    #    print "JSON"
    #    print component.getJsonRep()

    #print 'Getting component'
    #component = api.getComponentById(10)
    #print component.getDictRep()

    #print 'Adding component'
    #component = api.addComponent(name='wwz6', componentTypeId=8, createdByUserId=4, ownerUserId=4, ownerGroupId=3, isGroupWriteable=True, description='Test Component')
    #print "Added Component"
    #print component

    #component = api.getComponentById(10)
    #print component.getDictRep()
    api.setAdminGroupName('CDB_ADMIN')
    print api.addComponentPropertyByTypeId(componentId=10, propertyTypeId=2, tag='mytag', value='A', units=None, description=None, enteredByUserId=4, isDynamic=False, isUserWriteable=False)
    #print api.addComponentInstancePropertyByTypeId(componentInstanceId=50, propertyTypeId=2, tag='mytag', value='A', units=None, description=None, enteredByUserId=4, isDynamic=False, isUserWriteable=False)
