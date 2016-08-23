#!/usr/bin/env python

from dbLegacy.api.cdbDbApi import CdbDbApi
from dbLegacy.impl.propertyTypeHandler import PropertyTypeHandler
from dbLegacy.impl.propertyValueHandler import PropertyValueHandler
from dbLegacy.impl.propertyTypeHandlerHandler import PropertyTypeHandlerHandler
from dbLegacy.impl.propertyTypeCategoryHandler import PropertyTypeCategoryHandler


class PropertyDbApi(CdbDbApi):

    def __init__(self):
        CdbDbApi.__init__(self)
        self.propertyTypeHandler = PropertyTypeHandler()
        self.propertyValueHandler = PropertyValueHandler()
        self.propertyTypeHandlerHandler = PropertyTypeHandlerHandler()
        self.propertyTypeCategoryHandler = PropertyTypeCategoryHandler()

    @CdbDbApi.executeQuery
    def getPropertyValuesByPropertyTypeId(self, propertyTypeId, **kwargs):
        session = kwargs['session']
        dbPropertyType = self.propertyValueHandler.findPropertyValuesByPropertyTypeId(session, propertyTypeId)
        return self.toCdbObjectList(dbPropertyType)

    @CdbDbApi.executeQuery
    def getPropertyHandlerIdByPropertyHandlerName(self, propertyHandlerName, **kwargs):
        session = kwargs['session']
        dbPropertyTypeHandler = self.propertyTypeHandlerHandler.getPropertyTypeHandlerByName(session, propertyHandlerName)
        return dbPropertyTypeHandler.getCdbObject()

    @CdbDbApi.executeQuery
    def getPropertyTypesByHandlerId(self, propertyHandlerid, **kwargs):
        session = kwargs['session']
        dbPropertyTypes = self.propertyTypeHandler.getPropertyTypesByHandlerId(session, propertyHandlerid)
        return self.toCdbObjectList(dbPropertyTypes)

    @CdbDbApi.executeQuery
    def getPropertyTypeHandlers(self, **kwargs):
        session = kwargs['session']
        dbPropertyTypeHandlers = self.propertyTypeHandlerHandler.getPropertyTypeHandlers(session)
        return self.toCdbObjectList(dbPropertyTypeHandlers)

    @CdbDbApi.executeQuery
    def getPropertyTypeCategories(self, **kwargs):
        session = kwargs['session']
        dbPropertyTypeCategories = self.propertyTypeCategoryHandler.getPropertyTypeCategories(session)
        return self.toCdbObjectList(dbPropertyTypeCategories)

    @CdbDbApi.executeQuery
    def getPropertyTypes(self, **kwargs):
        session = kwargs['session']
        dbPropertyTypes = self.propertyTypeHandler.getPropertyTypes(session)
        return self.toCdbObjectList(dbPropertyTypes)


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


