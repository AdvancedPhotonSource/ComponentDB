#!/usr/bin/env python

#
# Implementation for design controller.
#

from cdb.common.objects.cdbObject import CdbObject
from cdb.common.objects.cdbObjectManager import CdbObjectManager
from cdb.common.db.api.designDbApi import DesignDbApi

class DesignControllerImpl(CdbObjectManager):
    """ Design controller implementation class. """

    def __init__(self):
        CdbObjectManager.__init__(self)
        self.designDbApi = DesignDbApi()

    def getDesigns(self):
        return self.designDbApi.getDesigns()

    def getDesignById(self, id):
        return self.designDbApi.getDesignById(id)

    def getDesignByName(self, name):
        return self.designDbApi.getDesignByName(name)

    def addDesign(self, name, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description):
        return self.designDbApi.addDesign(name, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description)

    def loadDesign(self, name, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description, designElementList):
        return self.designDbApi.loadDesign(name, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description, designElementList)
