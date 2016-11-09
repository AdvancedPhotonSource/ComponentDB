#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.exceptions.invalidArgument import InvalidArgument
from cdb.common.db.entities.propertyType import PropertyType
from cdb.common.db.entities.allowedPropertyValue import AllowedPropertyValue
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler
from cdb.common.db.impl.propertyTypeCategoryHandler import PropertyTypeCategoryHandler
from cdb.common.db.impl.propertyTypeHandlerHandler import PropertyTypeHandlerHandler

class PropertyTypeHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.propertyTypeCategoryHandler = PropertyTypeCategoryHandler()
        self.propertyTypeHandlerHandler = PropertyTypeHandlerHandler()

    def getPropertyTypes(self, session):
        return self._getAllDbObjects(session, PropertyType)

    def getPropertyTypesByHandlerId(self, session, propertyTypeHandlerId):
        try:
            dbPropertyTypes = session.query(PropertyType).filter(PropertyType.property_type_handler_id==propertyTypeHandlerId).all()
            return dbPropertyTypes
        except NoResultFound, ex:
            raise ObjectNotFound("Property types for handler id %s could not be found" % (propertyTypeHandlerId))

    def getPropertyTypeById(self, session, id):
        return self._findDbObjById(session, PropertyType, id)

    def getPropertyTypeByName(self, session, name):
        return self._findDbObjByName(session, PropertyType, name)

    def getAllowedPropertyTypeValuesById(self, session, propertyTypeId):
        dbAllowedPropertyTypeValues = session.query(AllowedPropertyValue).filter(AllowedPropertyValue.property_type_id==propertyTypeId).all()
        return dbAllowedPropertyTypeValues

    def addPropertyType(self, session, propertyTypeName, description, propertyTypeCategoryName, propertyTypeHandlerName,
                        defaultValue, defaultUnits, isUserWriteable, isDynamic, isInternal, isActive):
        self._prepareAddUniqueNameObj(session, PropertyType, propertyTypeName)
        entityDisplayName = self._getEntityDisplayName(PropertyType)

        dbPropertyType = PropertyType(name=propertyTypeName)
        dbPropertyType.description = description
        dbPropertyType.default_value = defaultValue
        dbPropertyType.default_units = defaultUnits
        dbPropertyType.is_user_writeable = isUserWriteable
        dbPropertyType.is_dynamic = isDynamic
        dbPropertyType.is_internal = isInternal
        dbPropertyType.is_active = isActive

        if propertyTypeCategoryName:
            dbPropertyTypeCategory = self.propertyTypeCategoryHandler.getPropertyTypeCategoryByName(session, propertyTypeCategoryName)
            dbPropertyType.propertyTypeCategory = dbPropertyTypeCategory
        if propertyTypeHandlerName:
            dbPropertyTypeHandler = self.propertyTypeHandlerHandler.getPropertyTypeHandlerByName(session, propertyTypeHandlerName)
            dbPropertyType.propertyTypeHandler = dbPropertyTypeHandler

        session.add(dbPropertyType)
        session.flush()

        self.logger.debug('Inserted %s id %s' % (entityDisplayName, dbPropertyType.id))
        return dbPropertyType

    def addAllowedPropertyValue(self, session, propertyTypeName, value, units, description, sortOrder):
        entityDisplayName = self._getEntityDisplayName(AllowedPropertyValue)

        dbPropertyType = self.getPropertyTypeByName(session, propertyTypeName)

        dbAllowedPropertyValue = AllowedPropertyValue()
        dbAllowedPropertyValue.propertyType = dbPropertyType
        dbAllowedPropertyValue.value = value
        dbAllowedPropertyValue.units = units
        dbAllowedPropertyValue.description = description
        dbAllowedPropertyValue.sort_order = sortOrder

        session.add(dbAllowedPropertyValue)
        session.flush()

        self.logger.debug('Inserted %s id: %s value: %s' % (entityDisplayName, dbAllowedPropertyValue.id, value))

        return dbAllowedPropertyValue

    @classmethod
    def checkPropertyValueIsAllowed(cls, propertyValue, dbAllowedPropertyValueList):
        # If allowed property value list is empty, value is ok
        if dbAllowedPropertyValueList is None or len(dbAllowedPropertyValueList) == 0:
            return

        for dbAllowedPropertyValue in dbAllowedPropertyValueList:
            if propertyValue == dbAllowedPropertyValue.value:
                return 
        raise InvalidArgument('Property value %s is not allowed.' % propertyValue)

