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

    def getComponentTypeCategories(self):
        try:
            session = self.dbManager.openSession()
            try:
                dbComponentTypeCategories = self.componentTypeCategoryHandler.getComponentTypeCategories(session)
                return self.toCdbObjectList(dbComponentTypeCategories)
            except CdbException, ex:
                raise
            except Exception, ex:
                self.logger.exception('%s' % ex)
                raise
        finally:
            self.dbManager.closeSession(session)

    def getComponentTypes(self):
        try:
            session = self.dbManager.openSession()
            try:
                dbComponentTypes = self.componentTypeHandler.getComponentTypes(session)
                return self.toCdbObjectList(dbComponentTypes)
            except CdbException, ex:
                raise
            except Exception, ex:
                self.logger.exception('%s' % ex)
                raise
        finally:
            self.dbManager.closeSession(session)

    def getComponents(self):
        try:
            session = self.dbManager.openSession()
            try:
                dbComponents = self.componentHandler.getComponents(session)
                return self.toCdbObjectList(dbComponents)
            except CdbException, ex:
                raise
            except Exception, ex:
                self.logger.exception('%s' % ex)
                raise
        finally:
            self.dbManager.closeSession(session)

    def getComponentById(self, id):
        try:
            session = self.dbManager.openSession()
            try:
                dbComponent = self.componentHandler.getComponentById(session, id)
                return dbComponent.getCdbObject()
            except CdbException, ex:
                raise
            except Exception, ex:
                self.logger.exception('%s' % ex)
                raise
        finally:
            self.dbManager.closeSession(session)

    def getComponentByName(self, name):
        try:
            session = self.dbManager.openSession()
            try:
                dbComponent = self.componentHandler.getComponentByName(session, name)
                return dbComponent.getCdbObject()
            except CdbException, ex:
                raise
            except Exception, ex:
                self.logger.exception('%s' % ex)
                raise
        finally:
            self.dbManager.closeSession(session)

    def addComponent(self, name, componentTypeId, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description):
        try:
             session = self.dbManager.openSession()
             try:
                 dbComponent = self.componentHandler.addComponent(session, name, componentTypeId, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description)
                 component = dbComponent.getCdbObject()
                 session.commit()
                 return component
             except CdbException, ex:
                 session.rollback()
                 raise
             except Exception, ex:
                 session.rollback()
                 self.logger.exception('%s' % ex)
                 raise
        finally:
            self.dbManager.closeSession(session)

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
    component = api.addComponent(name='bcd8', componentTypeId=8, createdByUserId=4, ownerUserId=4, ownerGroupId=3, isGroupWriteable=True, description='Test Component')
    print "Added Component"
    print component
