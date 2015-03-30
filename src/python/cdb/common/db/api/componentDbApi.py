#!/usr/bin/env python

from cdb.common.exceptions.cdbException import CdbException
from cdb.common.db.api.cdbDbApi import CdbDbApi
from cdb.common.db.impl.componentHandler import ComponentHandler
from cdb.common.db.impl.componentTypeHandler import ComponentTypeHandler
from cdb.common.db.impl.componentTypeCategoryHandler import ComponentTypeCategoryHandler

class ComponentDbApi(CdbDbApi):

    def __init__(self):
        CdbDbApi.__init__(self)
        self.componentTypeCategoryHandler = ComponentTypeCategoryHandler()
        self.componentTypeHandler = ComponentTypeHandler()
        self.componentHandler = ComponentHandler()

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

#######################################################################
# Testing.
if __name__ == '__main__':
    api = ComponentDbApi()
    componentTypeCategories = api.getComponentTypeCategories()
    for componentTypeCategory in componentTypeCategories:
        print componentTypeCategory

    componentTypes = api.getComponentTypes()
    for componentType in componentTypes:
        print componentType

    components = api.getComponents()
    for component in components:
        print
        print "********************"
        print component
        print "TEXT"
        print component.getTextRep()
        print "DICT"
        print component.getDictRep()
        print "JSON"
        print component.getJsonRep()

    print 'Getting component'
    component = api.getComponentById(10)
    print component.getDictRep()

    print 'Adding component'
    component = api.addComponent(name='wwz6', componentTypeId=8, createdByUserId=4, ownerUserId=4, ownerGroupId=3, isGroupWriteable=True, description='Test Component')
    print "Added Component"
    print component
