#!/usr/bin/env python

#
# Implementation for the PDMLink class
#

#######################################################################

from cdb.common.objects.cdbObjectManager import CdbObjectManager
from cdb.common.db.api.itemDbApi import ItemDbApi

class ItemControllerImpl(CdbObjectManager):
    def __init__(self):
        CdbObjectManager.__init__(self)
        self.itemDbApi = ItemDbApi()

    def getItemById(self, itemId):
        return self.itemDbApi.getItemById(itemId)

