#!/usr/bin/env python

import datetime

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.propertyValue import PropertyValue
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler
from userInfoHandler import UserInfoHandler
from propertyTypeHandler import PropertyTypeHandler

class PropertyValueHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.userInfoHandler = UserInfoHandler()
        self.propertyTypeHandler = PropertyTypeHandler()

    def getPropertyValues(self, session):
        self.logger.debug('Retrieving property value list')
        dbPropertyValues = session.query(PropertyValue).all()
        return dbPropertyValues

    def findPropertyValueById(self, session, id):
        try:
            dbPropertyValue = session.query(PropertyValue).filter(PropertyValue.id==id).one()
            return dbPropertyValue
        except NoResultFound, ex:
            raise ObjectNotFound('Property value id %s does not exist.' % (id))

    def getPropertyValueById(self, session, id):
        try:
            self.logger.debug('Retrieving property value id %s' % id)
            dbPropertyValue = self.findPropertyValueById(session, id)
            return dbPropertyValue
        except NoResultFound, ex:
            raise ObjectNotFound('Property value id %s does not exist.' % (id))

    def createPropertyValue(self, session, propertyTypeName, tag, value, units, description, enteredByUserId):
        enteredByDbUserInfo = self.userInfoHandler.getUserInfoById(session, enteredByUserId)
        enteredOnDateTime = datetime.datetime.now()
        dbPropertyType = self.propertyTypeHandler.getPropertyTypeByName(session, propertyTypeName)
        self.propertyTypeHandler.checkPropertyValueIsAllowed(value, dbPropertyType.allowedPropertyValueList)
        dbPropertyValue = PropertyValue(tag=tag, value=value, units=units, description=description, entered_on_date_time=enteredOnDateTime)
        dbPropertyValue.enteredByUserInfo = enteredByDbUserInfo
        dbPropertyValue.propertyType = dbPropertyType
        return dbPropertyValue

    def createPropertyValueByTypeId(self, session, propertyTypeId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable):
        enteredByDbUserInfo = self.userInfoHandler.getUserInfoById(session, enteredByUserId)
        enteredOnDateTime = datetime.datetime.now()
        dbPropertyType = self.propertyTypeHandler.getPropertyTypeById(session, propertyTypeId)
        self.propertyTypeHandler.checkPropertyValueIsAllowed(value, dbPropertyType.allowedPropertyValueList)
        dbPropertyValue = PropertyValue(property_type_id=propertyTypeId, tag=tag, value=value, units=units, description=description, entered_by_user_id=enteredByUserId, entered_on_date_time=enteredOnDateTime, is_dynamic=isDynamic, is_user_writeable=isUserWriteable)
        return dbPropertyValue

    def updatePropertyValueById(self, session, id, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable):
        dbPropertyValue = self.findPropertyValueById(session, id)
        enteredByDbUserInfo = self.userInfoHandler.getUserInfoById(session, enteredByUserId)
        enteredOnDateTime = datetime.datetime.now()
        dbPropertyType = self.propertyTypeHandler.getPropertyTypeById(session, dbPropertyValue.property_type_id)
        self.propertyTypeHandler.checkPropertyValueIsAllowed(value, dbPropertyType.allowedPropertyValueList)

        dbPropertyValue.entered_by_user_id = enteredByUserId
        dbPropertyValue.entered_on_date_time = enteredOnDateTime
        if tag is not None:
            dbPropertyValue.tag = tag
        if value is not None:
            dbPropertyValue.value = value
        if units is not None:
            dbPropertyValue.units = units
        if description is not None:
            dbPropertyValue.description = description
        if isDynamic is not None:
            dbPropertyValue.is_dynamic = isDynamic
        if isUserWriteable is not None:
            dbPropertyValue.is_user_writeable = isUserWriteable 
        session.add(dbPropertyValue)
        session.flush()
        return dbPropertyValue

    def createUnverifiedPropertyValue(self, session, propertyTypeName, tag, value, units, description, enteredByUserId):
        enteredOnDateTime = datetime.datetime.now()
        dbPropertyType = self.propertyTypeHandler.getPropertyTypeByName(session, propertyTypeName)
        self.propertyTypeHandler.checkPropertyValueIsAllowed(value, dbPropertyType.allowedPropertyValueList)
        dbPropertyValue = PropertyValue(tag=tag, value=value, units=units, description=description, entered_by_user_id=enteredByUserId, entered_on_date_time=enteredOnDateTime)
        dbPropertyValue.propertyType = dbPropertyType
        return dbPropertyValue

