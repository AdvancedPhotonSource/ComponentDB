#!/usr/bin/env python

from cdb.common.exceptions.cdbException import CdbException
from cdb.common.db.api.cdbDbApi import CdbDbApi
from cdb.common.db.impl.componentTypeHandler import ComponentTypeHandler

class ComponentDbApi(CdbDbApi):

    def __init__(self):
        CdbDbApi.__init__(self)
        self.componentTypeHandler = ComponentTypeHandler()

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


#######################################################################
# Testing.
if __name__ == '__main__':
    api = ComponentDbApi()
    componentTypeList = api.getComponentTypeList()
    for componentType in componentTypeList:
        print componentType.getJsonRep()


