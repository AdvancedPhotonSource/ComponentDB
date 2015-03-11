#!/usr/bin/env python

from cdb.common.exceptions.cdbException import CdbException
from cdb.common.db.api.cdbDbApi import CdbDbApi
from cdb.common.db.impl.designHandler import DesignHandler

class DesignDbApi(CdbDbApi):

    def __init__(self):
        CdbDbApi.__init__(self)
        self.designHandler = DesignHandler()

    def getDesigns(self):
        try:
            session = self.dbManager.openSession()
            try:
                dbDesigns = self.designHandler.getDesigns(session)
                return self.toCdbObjectList(dbDesigns)
            except CdbException, ex:
                raise
            except Exception, ex:
                self.logger.exception('%s' % ex)
                raise
        finally:
            self.dbManager.closeSession(session)

    def getDesignById(self, id):
        try:
            session = self.dbManager.openSession()
            try:
                dbDesign = self.designHandler.getDesignById(session, id)
                return dbDesign.getCdbObject()
            except CdbException, ex:
                raise
            except Exception, ex:
                self.logger.exception('%s' % ex)
                raise
        finally:
            self.dbManager.closeSession(session)

    def getDesignByName(self, name):
        try:
            session = self.dbManager.openSession()
            try:
                dbDesign = self.designHandler.getDesignByName(session, name)
                return dbDesign.getCdbObject()
            except CdbException, ex:
                raise
            except Exception, ex:
                self.logger.exception('%s' % ex)
                raise
        finally:
            self.dbManager.closeSession(session)

    def addDesign(self, name, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description):
        try:
             session = self.dbManager.openSession()
             try:
                 dbDesign = self.designHandler.addDesign(session, name, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description)
                 design = dbDesign.getCdbObject()
                 session.commit()
                 return design
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
    design = api.addDesign(name='bcd8', createdByUserId=4, ownerUserId=4, ownerGroupId=3, isGroupWriteable=True, description='Test Design')
    print "Added Design"
    print design
