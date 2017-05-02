#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.db.api.cdbDbApi import CdbDbApi
from cdb.common.db.impl.propertyTypeHandler import PropertyTypeHandler
from cdb.common.db.impl.propertyValueHandler import PropertyValueHandler
from cdb.common.db.impl.propertyTypeHandlerHandler import PropertyTypeHandlerHandler
from cdb.common.db.impl.propertyTypeCategoryHandler import PropertyTypeCategoryHandler


class PropertyDbApi(CdbDbApi):

    def __init__(self):
        CdbDbApi.__init__(self)
        self.propertyTypeHandler = PropertyTypeHandler()
        self.propertyValueHandler = PropertyValueHandler()
        self.propertyTypeHandlerHandler = PropertyTypeHandlerHandler()
        self.propertyTypeCategoryHandler = PropertyTypeCategoryHandler()

    @CdbDbApi.executeQuery
    def getPropertyValuesByPropertyTypeId(self, propertyTypeId, **kwargs):
        """
        Get all property values with a given property type id.

        :param propertyTypeId:
        :param kwargs:
        :return: CdbObject List of resulting records.
        """
        session = kwargs['session']
        dbPropertyType = self.propertyValueHandler.findPropertyValuesByPropertyTypeId(session, propertyTypeId)
        return self.toCdbObjectList(dbPropertyType)

    @CdbDbApi.executeQuery
    def getPropertyHandlerTypeHandlerByPropertyHandlerName(self, propertyHandlerName, **kwargs):
        """
        Get a property type handler by its name.

        :param propertyHandlerName:
        :param kwargs:
        :return: (CdbObject) resulting record.
        """
        session = kwargs['session']
        dbPropertyTypeHandler = self.propertyTypeHandlerHandler.getPropertyTypeHandlerByName(session, propertyHandlerName)
        return dbPropertyTypeHandler.getCdbObject()

    @CdbDbApi.executeQuery
    def getPropertyTypesByHandlerId(self, propertyHandlerid, **kwargs):
        """
        Get all property types by their handler id.

        :param propertyHandlerid:
        :param kwargs:
        :return: CdbObject List of resulting records.
        """
        session = kwargs['session']
        dbPropertyTypes = self.propertyTypeHandler.getPropertyTypesByHandlerId(session, propertyHandlerid)
        return self.toCdbObjectList(dbPropertyTypes)

    @CdbDbApi.executeTransaction
    def addPropertyTypeHandler(self, propertyTypeHandlerName, description, **kwargs):
        """
        Add a property type handler.

        :param propertyTypeHandlerName:
        :param description:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbPropertyTypeHandler = self.propertyTypeHandlerHandler.addPropertyTypeHandler(session, propertyTypeHandlerName, description)
        return dbPropertyTypeHandler.getCdbObject()

    @CdbDbApi.executeQuery
    def getPropertyTypeHandlers(self, **kwargs):
        """
        Get all property type handler records.

        :param kwargs:
        :return: CdbObject List of resulting records.
        """
        session = kwargs['session']
        dbPropertyTypeHandlers = self.propertyTypeHandlerHandler.getPropertyTypeHandlers(session)
        return self.toCdbObjectList(dbPropertyTypeHandlers)

    @CdbDbApi.executeQuery
    def getPropertyTypes(self, **kwargs):
        """
        Get all property type records.

        :param kwargs:
        :return: CdbObject List of resulting records.
        """
        session = kwargs['session']
        dbPropertyTypes = self.propertyTypeHandler.getPropertyTypes(session)
        return self.toCdbObjectList(dbPropertyTypes)

    @CdbDbApi.executeQuery
    def getPropertyTypeById(self, propertyTypeId, **kwargs):
        """
        Get property type record by id.

        :param propertyTypeId:
        :param kwargs:
        :return:
        """
        session = kwargs['session']
        dbPropertyType = self.propertyTypeHandler.getPropertyTypeById(session, propertyTypeId)
        return dbPropertyType.toCdbObject()

    @CdbDbApi.executeQuery
    def getAllowedPropertyValuesForPropertyType(self, propertyTypeId, **kwargs):
        """
        Get a list of allowed property values for a certain property type

        :param propertyTypeId:
        :param kwargs:
        :return:
        """
        session = kwargs['session']
        dbAllowedPropertyValues = self.propertyTypeHandler.getAllowedPropertyTypeValuesById(session, propertyTypeId)
        return  self.toCdbObjectList(dbAllowedPropertyValues)

    @CdbDbApi.executeQuery
    def getPropertyTypeCategories(self, **kwargs):
        """
        Get all property type category records.

        :param kwargs:
        :return: CdbObject List of resulting records.
        """
        session = kwargs['session']
        dbPropertyTypeCategories = self.propertyTypeCategoryHandler.getPropertyTypeCategories(session)
        return self.toCdbObjectList(dbPropertyTypeCategories)

    @CdbDbApi.executeTransaction
    def addPropertyTypeCategory(self, propertyTypeCategoryName, description, **kwargs):
        """
        Add a property type category record.

        :param propertyTypeCategoryName:
        :param description:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbPropertyTypeCategories = self.propertyTypeCategoryHandler.addPropertyTypeCategory(session, propertyTypeCategoryName, description)
        return dbPropertyTypeCategories.getCdbObject()

    @CdbDbApi.executeTransaction
    def addPropertyType(self, propertyTypeName, description, propertyTypeCategoryName, propertyTypeHandlerName,
                        defaultValue, defaultUnits, isUserWriteable, isDynamic, isInternal, isActive, **kwargs):
        """
        Add a property type record.

        :param propertyTypeName:
        :param description:
        :param propertyTypeCategoryName:
        :param propertyTypeHandlerName:
        :param defaultValue:
        :param defaultUnits:
        :param isUserWriteable:
        :param isDynamic:
        :param isInternal:
        :param isActive:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbPropertyType = self.propertyTypeHandler.addPropertyType(session, propertyTypeName, description,
                                                                  propertyTypeCategoryName, propertyTypeHandlerName,
                                                                  defaultValue, defaultUnits, isUserWriteable,
                                                                  isDynamic, isInternal, isActive)
        return dbPropertyType.toCdbObject()

    @CdbDbApi.executeTransaction
    def addAllowedPropertyValue(self, propertyTypeName, value, units, description, sortOrder, **kwargs):
        """
        Add an allowed property value record.

        :param propertyTypeName:
        :param value:
        :param units:
        :param description:
        :param sortOrder:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbAllowedPropertyValue = self.propertyTypeHandler.addAllowedPropertyValue(session, propertyTypeName, value, units, description, sortOrder)
        return dbAllowedPropertyValue.toCdbObject()

    @CdbDbApi.executeQuery
    def getPropertyValueListForItemElementId(self, itemElementId, propertyTypeName = None, **kwargs):
        """
        Get a property value list for a particular item element id.

        NOTE: Item properties are in their 'self element'.
        :param itemElementId:
        :param propertyTypeName:
        :param kwargs:
        :return: CdbObject List of resulting records.
        """
        session = kwargs['session']
        dbPropertyValues = self.propertyValueHandler.getPropertyValueListForItemElementId(session, itemElementId, propertyTypeName)
        return self.toCdbObjectList(dbPropertyValues)

    @CdbDbApi.executeQuery
    def getPropertyMetadataForPropertyValueId(self, propertyValueId, **kwargs):
        """
        Get a property value metadata list for a property value with a certain id.

        :param propertyValueId:
        :param kwargs:
        :return: CdbObject List of resulting records
        """
        session = kwargs['session']
        dbPropertyMetadata = self.propertyValueHandler.getPropertyValueMetadata(session, propertyValueId)
        return self.toCdbObjectList(dbPropertyMetadata)

    @CdbDbApi.executeTransaction
    def addPropertyMetadataForPropertyValueId(self, propertyValueId, metadataKey, metadataValue, userId, **kwargs):
        """
        Add propertyMetadata for a certain property value.

        :param propertyValueId:
        :param metadataKey:
        :param metadataValue:
        :param kwargs:
        :return:
        """
        session = kwargs['session']
        dbProperyMetadata = self.propertyValueHandler.addPropertyValueMetadata(session, propertyValueId, metadataKey, metadataValue, userId)
        return dbProperyMetadata.toCdbObject()

    @CdbDbApi.executeTransaction
    def addPropertyValueMetadataFromDict(self, propertyValueId, propertyValueMetadataKeyValueDict, userId, **kwargs):
        """
        Add propertyMetadata for a certain property value using a key value dict.

        :param propertyValueId:
        :param propertyValueMetadataKeyValueDict:
        :param kwargs:
        :return:
        """
        session = kwargs['session']
        dbPropertyMetadata = self.propertyValueHandler.addPropertyValueMetadataFromDict(session, propertyValueId, propertyValueMetadataKeyValueDict, userId)
        return self.toCdbObjectList(dbPropertyMetadata)



#######################################################################
# Testing.
if __name__ == '__main__':
    api = PropertyDbApi()

    def appendNonEmptyValue(array, value):
        if value is None or value == "":
            return
        array.append(value)

    def getPropertyValueListByPropertyTypeHandler(handlerName):
        propertyTypeHandler = api.getPropertyHandlerIdByPropertyHandlerName(handlerName)
        if propertyTypeHandler is not None:
            handlerPropertyTypes = api.getPropertyTypesByHandlerId(propertyTypeHandler.data['id'])
        else:
            return

        propertyValues = []
        for propertyType in handlerPropertyTypes:
            propertyTypeValues = api.getPropertyValuesByPropertyTypeId(propertyType.data['id'])
            propertyValues = propertyValues + propertyTypeValues

        attachements = []
        for propertyValue in propertyValues:
            appendNonEmptyValue(attachements,propertyValue.data['value'])
            for propertyValueHistory in propertyValue.data['propertyValueHistory']:
                appendNonEmptyValue(attachements, propertyValueHistory.value)

        return attachements

    imageAttachments = getPropertyValueListByPropertyTypeHandler('Image')
    documentAttachments = getPropertyValueListByPropertyTypeHandler('Document')

    print imageAttachments
    print documentAttachments


