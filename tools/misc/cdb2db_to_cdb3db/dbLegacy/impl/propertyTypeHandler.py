#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.exceptions.invalidArgument import InvalidArgument
from dbLegacy.entities.propertyType import PropertyType
from dbLegacy.entities.allowedPropertyValue import AllowedPropertyValue
from dbLegacy.impl.cdbDbEntityHandler import CdbDbEntityHandler

class PropertyTypeHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def getPropertyTypes(self, session):
        dbPropertyTypes = session.query(PropertyType).all()
        return dbPropertyTypes

    def findPropertyTypeById(self, session, id):
        try:
            dbPropertyType = session.query(PropertyType).filter(PropertyType.id==id).one()
            return dbPropertyType
        except NoResultFound, ex:
            raise ObjectNotFound('Property type id %s does not exist.' % (id))

    def getPropertyTypesByHandlerId(self, session, propertyTypeHandlerId):
        try:
            dbPropertyTypes = session.query(PropertyType).filter(PropertyType.property_type_handler_id==propertyTypeHandlerId).all()
            return dbPropertyTypes
        except NoResultFound, ex:
            raise ObjectNotFound("Property types for handler id %s could not be found" % (propertyTypeHandlerId))

    def getPropertyTypeById(self, session, id):
        try:
            dbPropertyType = session.query(PropertyType).filter(PropertyType.id==id).one()
            dbPropertyType.allowedPropertyValueList = self.getAllowedPropertyTypeValuesById(session, dbPropertyType.id)
            return dbPropertyType
        except NoResultFound, ex:
            raise ObjectNotFound('Property type id %s does not exist.' % (id))

    def getPropertyTypeByName(self, session, name):
        try:
            dbPropertyType = session.query(PropertyType).filter(PropertyType.name==name).one()
            dbPropertyType.allowedPropertyValueList = self.getAllowedPropertyTypeValuesById(session, dbPropertyType.id)
            return dbPropertyType
        except NoResultFound, ex:
            raise ObjectNotFound('Property type name %s does not exist.' % (name))

    def getAllowedPropertyTypeValuesById(self, session, propertyTypeId):
        dbAllowedPropertyTypeValues = session.query(AllowedPropertyValue).filter(AllowedPropertyValue.property_type_id==propertyTypeId).all()
        return dbAllowedPropertyTypeValues

    @classmethod
    def checkPropertyValueIsAllowed(cls, propertyValue, dbAllowedPropertyValueList):
        # If allowed property value list is empty, value is ok
        if dbAllowedPropertyValueList is None or len(dbAllowedPropertyValueList) == 0:
            return

        for dbAllowedPropertyValue in dbAllowedPropertyValueList:
            if propertyValue == dbAllowedPropertyValue.value:
                return 
        raise InvalidArgument('Property value %s is not allowed.' % propertyValue)

