#!/usr/bin/env python

from cdb.common.exceptions.cdbException import CdbException
from cdb.common.db.api.cdbDbApi import CdbDbApi
from cdb.common.db.impl.designHandler import DesignHandler
from cdb.common.db.impl.designElementHandler import DesignElementHandler

class DesignDbApi(CdbDbApi):

    def __init__(self):
        CdbDbApi.__init__(self)
        self.designHandler = DesignHandler()
        self.designElementHandler = DesignElementHandler()

    @CdbDbApi.executeQuery
    def getDesigns(self, **kwargs):
        session = kwargs['session']
        dbDesigns = self.designHandler.getDesigns(session)
        return self.toCdbObjectList(dbDesigns)

    @CdbDbApi.executeQuery
    def getDesignById(self, id, **kwargs):
        session = kwargs['session']
        dbDesign = self.designHandler.getDesignById(session, id)
        return dbDesign.getCdbObject()

    @CdbDbApi.executeQuery
    def getDesignByName(self, name, **kwargs):
        session = kwargs['session']
        dbDesign = self.designHandler.getDesignByName(session, name)
        return dbDesign.getCdbObject()

    @CdbDbApi.executeTransaction
    def addDesign(self, name, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description, **kwargs):
        session = kwargs['session']
        dbDesign = self.designHandler.addDesign(session, name, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description)
        return dbDesign.getCdbObject()

    @CdbDbApi.executeQuery
    def getDesignElements(self, designId, **kwargs):
        session = kwargs['session']
        dbDesignElements = self.designElementHandler.getDesignElements(session, designId)
        return self.toCdbObjectList(dbDesignElements)

    @CdbDbApi.executeTransaction
    def addDesignElement(self, name, parentDesignId, childDesignId, componentId, locationId, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, sortOrder, description, **kwargs):
        session = kwargs['session']
        dbDesignElement = self.designElementHandler.addDesignElement(session, name, parentDesignId, childDesignId, componentId, locationId, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, sortOrder, description)
        return dbDesignElement.getCdbObject()

#######################################################################
# Testing.
if __name__ == '__main__':
    api = DesignDbApi()
    designs = api.getDesigns()
    for design in designs:
        print
        print "********************"
        print design
        print "TEXT"
        print design.getTextRep()
        print "DICT"
        print design.getDictRep()
        print "JSON"
        print design.getJsonRep()

    print 'Getting design'
    design = api.getDesignById(1)
    print design.getDictRep()

    print 'Adding design'
    design = api.addDesign(name='de6', createdByUserId=4, ownerUserId=4, ownerGroupId=3, isGroupWriteable=True, description='Test Design')
    print "Added Design"
    print design

    print 'Getting design elements'
    parentDesignId = 1
    designElements = api.getDesignElements(parentDesignId)
    for designElement in designElements:
        print
        print "********************"
        print designElement

    print 'Adding design element'
    designElement = api.addDesignElement(name='elm2', parentDesignId=1, childDesignId=2, componentId=None, locationId=None, createdByUserId=4, ownerUserId=4, ownerGroupId=3, isGroupWriteable=True, sortOrder=111.123, description='Test Design Element')
    print "Added Design Element"
    print designElement

