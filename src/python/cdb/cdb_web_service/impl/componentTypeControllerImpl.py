#!/usr/bin/env python

#
# Implementation for component type controller.
#

#######################################################################

from cdb.common.objects.cdbObject import CdbObject
from cdb.common.objects.cdbObjectManager import CdbObjectManager
from cdb.common.db.api.componentDbApi import ComponentDbApi

#######################################################################

class ComponentTypeControllerImpl(CdbObjectManager):
    """ Component type controller implementation class. """

    def __init__(self):
        CdbObjectManager.__init__(self)
        self.componentDbApi = ComponentDbApi()

    def getComponentTypes(self):
        return self.componentDbApi.getComponentTypes()

