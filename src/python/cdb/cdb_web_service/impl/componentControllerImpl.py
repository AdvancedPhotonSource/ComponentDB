#!/usr/bin/env python

#
# Implementation for component controller.
#

from cdb.common.objects.cdbObject import CdbObject
from cdb.common.objects.cdbObjectManager import CdbObjectManager
from cdb.common.db.api.componentDbApi import ComponentDbApi

class ComponentControllerImpl(CdbObjectManager):
    """ Component controller implementation class. """

    def __init__(self):
        CdbObjectManager.__init__(self)
        self.componentDbApi = ComponentDbApi()

    def getComponents(self):
        return self.componentDbApi.getComponents()

    def getComponentById(self, id):
        return self.componentDbApi.getComponentById(id)

    def getComponentByName(self, name):
        return self.componentDbApi.getComponentByName(name)

    def addComponent(self, name, componentTypeId, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description):
        return self.componentDbApi.addComponent(name, componentTypeId, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description)
