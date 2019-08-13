#!/usr/bin/env python

#
# Implementation for the PDMLink class
#

#######################################################################

from cdb.common.objects.cdbObjectManager import CdbObjectManager
from cdb.common.db.api.itemDbApi import ItemDbApi


class ItemElementControllerImpl(CdbObjectManager):

    def __init__(self):
        CdbObjectManager.__init__(self)
        self.itemDbApi = ItemDbApi()

    def getItemElementById(self, itemElementId):
        return self.itemDbApi.getItemElementById(itemElementId)
