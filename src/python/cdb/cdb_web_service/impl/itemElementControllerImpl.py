#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Implementation for the Item Element class
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

    def addItemElement(self, parentItemId, name, createdByUserId, ownerUserId, ownerGroupId,
       containedItemId=None, description=None, isRequired=None, isGroupWriteable=True):
        return self.itemDbApi.addItemElement(name, parentItemId, containedItemId, isRequired, description,
                                      createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable)

    def addPropertyValueForItemElementWithId(self, itemElementId, propertyTypeName, enteredByUserId,
                                      tag=None, value=None, units=None, description=None,
                                      isUserWriteable=None, isDynamic=False):
        propertyValueAdded = self.itemDbApi.addItemElementProperty(itemElementId, propertyTypeName,
                                                                   tag, value, units, description,
                                                                   enteredByUserId, isUserWriteable, isDynamic)

        return propertyValueAdded
