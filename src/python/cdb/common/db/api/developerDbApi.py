#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.db.api.cdbDbApi import CdbDbApi
from cdb.common.db.impl.propertyValueHandler import PropertyValueHandler
from cdb.common.db.impl.itemHandler import ItemHandler


class DeveloperDbApi(CdbDbApi):

    def __init__(self):
        CdbDbApi.__init__(self)
        self.propertyValueHandler = PropertyValueHandler()
        self.itemHandler = ItemHandler()

    @CdbDbApi.executeTransaction
    def addPropertyValueHistory(self, propertyValueId, tag, value, units, description, enteredByUserId, enteredOnDateTime, displayValue, targetValue, **kwargs):
        session = kwargs['session']
        dbPropertyValueHistory = self.propertyValueHandler.addPropertyValueHistory(session, propertyValueId, tag, value, units, description, enteredByUserId, enteredOnDateTime, displayValue, targetValue)
        return dbPropertyValueHistory.toCdbObject()

    @CdbDbApi.executeTransaction
    def addItemElementRelationshipHistory(self, itemRelationshipId, firstItemElementId, secondItemElementId, firstItemConnectorId, secondItemConnectorId,
                                   linkItemElementId, relationshipDetails, resourceTypeName, label, description, enteredByUserId, enteredOnDateTime, **kwargs):
        session = kwargs['session']
        dbItemElementRelationshipHistory = self.itemHandler.addItemElementRelationshipHistory(session, itemRelationshipId, firstItemElementId, secondItemElementId, firstItemConnectorId, secondItemConnectorId,
                                                                                              linkItemElementId, relationshipDetails, resourceTypeName, label, description, enteredByUserId, enteredOnDateTime )
        return dbItemElementRelationshipHistory.toCdbObject()



#######################################################################
# Testing.
if __name__ == '__main__':
    api = DeveloperDbApi()

