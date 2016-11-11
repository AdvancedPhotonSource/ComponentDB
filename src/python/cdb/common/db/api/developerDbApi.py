#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.db.api.cdbDbApi import CdbDbApi
from cdb.common.db.impl.propertyValueHandler import PropertyValueHandler
from cdb.common.db.impl.itemHandler import ItemHandler


class DeveloperDbApi(CdbDbApi):
    """
    Db apis that perform updates/inserts with specific dates,
    should only be used if moving data with date importantce.

    Usually, dates should just be auto-generated at the time of adding to db.
    """

    def __init__(self):
        CdbDbApi.__init__(self)
        self.propertyValueHandler = PropertyValueHandler()
        self.itemHandler = ItemHandler()

    @CdbDbApi.executeTransaction
    def addPropertyValueHistory(self, propertyValueId, tag, value, units, description, enteredByUserId, enteredOnDateTime, displayValue, targetValue, **kwargs):
        """
        Adds a property value history record.

        :param propertyValueId: id of the property value history is being added for.
        :param tag:
        :param value:
        :param units:
        :param description:
        :param enteredByUserId:
        :param enteredOnDateTime:
        :param displayValue:
        :param targetValue:
        :param kwargs:
        :return: newly added record
        """
        session = kwargs['session']
        dbPropertyValueHistory = self.propertyValueHandler.addPropertyValueHistory(session, propertyValueId, tag, value, units, description, enteredByUserId, enteredOnDateTime, displayValue, targetValue)
        return dbPropertyValueHistory.toCdbObject()

    @CdbDbApi.executeTransaction
    def addItemElementRelationshipHistory(self, itemRelationshipId, firstItemElementId, secondItemElementId, firstItemConnectorId, secondItemConnectorId,
                                   linkItemElementId, relationshipDetails, resourceTypeName, label, description, enteredByUserId, enteredOnDateTime, **kwargs):
        """
        Adds an item element relationship history record.

        :param itemRelationshipId: id of the relationship that history is being added for.
        :param firstItemElementId:
        :param secondItemElementId:
        :param firstItemConnectorId:
        :param secondItemConnectorId:
        :param linkItemElementId:
        :param relationshipDetails:
        :param resourceTypeName:
        :param label:
        :param description:
        :param enteredByUserId:
        :param enteredOnDateTime:
        :param kwargs:
        :return: newly added record.
        """
        session = kwargs['session']
        dbItemElementRelationshipHistory = self.itemHandler.addItemElementRelationshipHistory(session, itemRelationshipId, firstItemElementId, secondItemElementId, firstItemConnectorId, secondItemConnectorId,
                                                                                              linkItemElementId, relationshipDetails, resourceTypeName, label, description, enteredByUserId, enteredOnDateTime )
        return dbItemElementRelationshipHistory.toCdbObject()



#######################################################################
# Testing.
if __name__ == '__main__':
    api = DeveloperDbApi()

