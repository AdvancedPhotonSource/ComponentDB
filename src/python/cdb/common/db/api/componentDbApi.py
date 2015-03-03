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

    def getComponentTypeCategoryList(self):
        try:
            session = self.dbManager.openSession()
            try:
                dbComponentTypeCategoryList = self.componentTypeCategoryHandler.getComponentTypeCategoryList(session)
                return self.toCdbObjectList(dbComponentTypeCategoryList)
            except CdbException, ex:
                raise
            except Exception, ex:
                self.logger.exception('%s' % ex)
                raise
        finally:
            self.dbManager.closeSession(session)

    def getComponentTypeList(self):
        try:
            session = self.dbManager.openSession()
            try:
                dbComponentTypeList = self.componentTypeHandler.getComponentTypeList(session)
                return self.toCdbObjectList(dbComponentTypeList)
            except CdbException, ex:
                raise
            except Exception, ex:
                self.logger.exception('%s' % ex)
                raise
        finally:
            self.dbManager.closeSession(session)

    def getComponentList(self):
        try:
            session = self.dbManager.openSession()
            try:
                dbComponentList = self.componentHandler.getComponentList(session)
                return self.toCdbObjectList(dbComponentList)
            except CdbException, ex:
                raise
            except Exception, ex:
                self.logger.exception('%s' % ex)
                raise
        finally:
            self.dbManager.closeSession(session)


#######################################################################
# Testing.
if __name__ == '__main__':
    api = ComponentDbApi()
    componentTypeCategoryList = api.getComponentTypeCategoryList()
    for componentTypeCategory in componentTypeCategoryList:
        print componentTypeCategory

    componentTypeList = api.getComponentTypeList()
    for componentType in componentTypeList:
        print componentType

    componentList = api.getComponentList()
    for component in componentList:
        print
        print "********************"
        print component
        print "TEXT"
        print component.getTextRep()
        print "DICT"
        print component.getDictRep()
        print "JSON"
        print component.getJsonRep()


