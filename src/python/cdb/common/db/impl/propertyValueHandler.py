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

    def getPropertyValueById(self, session, id):
        try:
            self.logger.debug('Retrieving property value id %s' % id)
            dbPropertyValue = session.query(PropertyValue).filter(PropertyValue.id==id).one()
            return dbPropertyValue
        except NoResultFound, ex:
            raise ObjectNotFound('Property value id %s does not exist.' % (id))

    def createPropertyValue(self, session, propertyTypeName, tag, value, units, description, enteredByUserId):
        enteredByDbUserInfo = self.userInfoHandler.getUserInfoById(session, enteredByUserId)
        enteredOnDateTime = datetime.datetime.now()
        dbPropertyType = self.propertyTypeHandler.getPropertyTypeByName(session, propertyTypeName)
        dbPropertyValue = PropertyValue(tag=tag, value=value, units=units, description=description, entered_on_date_time=enteredOnDateTime)
        dbPropertyValue.enteredByUserInfo = enteredByDbUserInfo
        dbPropertyValue.propertyType = dbPropertyType
        return dbPropertyValue

