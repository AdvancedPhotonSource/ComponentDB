#!/usr/bin/env python

#
# Implementation for user info controller.
#

#######################################################################

import threading

from cdb.common.objects.cdbObject import CdbObject
from cdb.common.objects.cdbObjectManager import CdbObjectManager
from cdb.common.db.api.componentDbApi import ComponentDbApi

#######################################################################

class ComponentControllerImpl(CdbObjectManager):
    """ Component controller implementation class. """

    def __init__(self):
        CdbObjectManager.__init__(self)
        self.componentDbApi = UserInfoDbApi()

    def getComponentTypeCategoryList(self):
        return self.componentDbApi.getComponentTypeCategoryList()

    def getComponentTypeList(self):
        return self.componentDbApi.getComponentTypeList()

