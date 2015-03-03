#!/usr/bin/env python

#
# Implementation for component type category controller.
#

#######################################################################

from cdb.common.objects.cdbObject import CdbObject
from cdb.common.objects.cdbObjectManager import CdbObjectManager
from cdb.common.db.api.componentDbApi import ComponentDbApi

#######################################################################

class ComponentTypeCategoryControllerImpl(CdbObjectManager):
    """ Component type category controller implementation class. """

    def __init__(self):
        CdbObjectManager.__init__(self)
        self.componentDbApi = ComponentDbApi()

    def getComponentTypeCategoryList(self):
        return self.componentDbApi.getComponentTypeCategoryList()
