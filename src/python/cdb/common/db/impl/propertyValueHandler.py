#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import datetime

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.exceptions.commandFailed import CommandFailed
from cdb.common.db.entities.propertyValue import PropertyValue
from cdb.common.db.entities.propertyValueHistory import PropertyValueHistory
from cdb.common.db.entities.itemElementProperty import ItemElementProperty
from cdb.common.db.entities.propertyType import PropertyType
from cdb.common.db.entities.propertyMetadata import PropertyMetadata
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler
from cdb.common.db.impl.userInfoHandler import UserInfoHandler
from cdb.common.db.impl.propertyTypeHandler import PropertyTypeHandler
from cdb.common.db.impl.permissionHandler import PermissionHandler

class PropertyValueHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.userInfoHandler = UserInfoHandler()
        self.propertyTypeHandler = PropertyTypeHandler()
        self.permissionHandler = PermissionHandler()

    def getPropertyValues(self, session):
        self.logger.debug('Retrieving property value list')
        dbPropertyValues = session.query(PropertyValue).all()
        return dbPropertyValues

    def findPropertyValuesByPropertyTypeId(self, session, propertyTypeId):
        try:
            dbPropertyValueList = session.query(PropertyValue).filter(PropertyValue.property_type_id==propertyTypeId).all()
            return dbPropertyValueList
        except NoResultFound, ex:
            raise ObjectNotFound('Property value(s) with property type id %s do(es) not exists' % (propertyTypeId))

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

    def addPropertyValueHistory(self, session, propertyValueId, tag, value, units, description, enteredByUserId, enteredOnDateTime, displayValue, targetValue):
        entityDisplayName = self._getEntityDisplayName(PropertyValueHistory)
        dbPropertyValue = self.getPropertyValueById(session, propertyValueId)

        dbPropertyValueHistory = PropertyValueHistory()
        dbPropertyValueHistory.tag = tag
        dbPropertyValueHistory.value = value
        dbPropertyValueHistory.units = units
        dbPropertyValueHistory.description = description
        dbPropertyValueHistory.entered_by_user_id = enteredByUserId
        dbPropertyValueHistory.entered_on_date_time = enteredOnDateTime
        dbPropertyValueHistory.display_value = displayValue
        dbPropertyValueHistory.target_value = targetValue
        dbPropertyValueHistory.property_value_id = dbPropertyValue.id

        session.add(dbPropertyValueHistory)
        session.flush()

        self.logger.debug('Inserted %s id %s' % (entityDisplayName, dbPropertyValueHistory.id))

        return dbPropertyValueHistory

    def getPropertyValueMetadata(self, session, propertyValueId):
        self.logger.debug('Retrieving metadata for property value id %s' % propertyValueId)

        query = session.query(PropertyMetadata)
        query = query.filter(PropertyMetadata.property_value_id == propertyValueId)

        results = query.all()

        return results

    def getPropertyValueMetadataByKey(self, session, propertyValueId, metadataKey):
        self.logger.debug('Retrieving metadata for property value id %s with key %s' % (propertyValueId, metadataKey))

        query = session.query(PropertyMetadata)
        query = query.filter(PropertyMetadata.property_value_id == propertyValueId)
        query = query.filter(PropertyMetadata.metadata_key == metadataKey)

        try:
            dbPropertyMetadata = query.one()
            return dbPropertyMetadata
        except NoResultFound, ex:
            raise ObjectNotFound('Metadata for property value id %s with key %s do(es) not exists' % (propertyValueId, metadataKey))

    def addPropertyValueMetadata(self, session, propertyValueId, metadataKey, metadataValue, userId):
        entityDisplayName = self._getEntityDisplayName(PropertyMetadata)

        try:
            self.getPropertyValueMetadataByKey(session, propertyValueId, metadataKey)
            raise ObjectAlreadyExists ('%s with key %s for property id %s already exists.' % (entityDisplayName, metadataKey, propertyValueId))
        except ObjectNotFound, ex:
            # ok
            pass

        dbPropertyValue = self.getPropertyValueById(session, propertyValueId)

        self.permissionHandler.verifyPermissionsToUpdatePropertyValue(session, dbPropertyValue, userId)

        dbPropertyMetadata = PropertyMetadata()

        dbPropertyMetadata.propertyValue = dbPropertyValue
        dbPropertyMetadata.metadata_key = metadataKey
        dbPropertyMetadata.metadata_value = metadataValue

        session.add(dbPropertyMetadata)
        session.flush()

        self.logger.debug('Inserted %s id %s' % (entityDisplayName, dbPropertyMetadata.id))

        return dbPropertyMetadata

    def addPropertyValueMetadataFromDict(self, session, propertyValueId, propertyValueMetadataKeyValueDict, userId):
        entityDisplayName = self._getEntityDisplayName(PropertyMetadata)

        dbPropertyValue = self.getPropertyValueById(session, propertyValueId)
        self.permissionHandler.verifyPermissionsToUpdatePropertyValue(session, dbPropertyValue, userId)

        dbMetadataAdded = []

        dbStoredPropertyMetadataList = self.getPropertyValueMetadata(session, propertyValueId)

        for metadataKey in propertyValueMetadataKeyValueDict:
            dbPropertyMetadata = None

            # Verify if key already exists
            for dbStoredPropertyMetadata in dbStoredPropertyMetadataList:
                if (dbStoredPropertyMetadata.metadata_key == metadataKey):
                    dbPropertyMetadata = dbStoredPropertyMetadata
                    break

            # Add a nonexistent key
            if dbPropertyMetadata is None:
                dbPropertyMetadata = PropertyMetadata()
                dbPropertyMetadata.propertyValue = dbPropertyValue
                dbPropertyMetadata.metadata_key = metadataKey

            dbPropertyMetadata.metadata_value = propertyValueMetadataKeyValueDict[metadataKey]

            session.add(dbPropertyMetadata)
            dbMetadataAdded.append(dbPropertyMetadata)

        session.flush()

        self.logger.debug('Inserted %s id values for property id %s: %s' % (entityDisplayName, propertyValueId, dbMetadataAdded))

        return dbMetadataAdded

    def createPropertyValue(self, session, propertyTypeName, tag, value, units, description, enteredByUserId, isUserWriteable = None, isDynamic = None, displayValue = None, targetValue = None, enteredOnDateTime = None, allowInternal=False):
        enteredByDbUserInfo = self.userInfoHandler.getUserInfoById(session, enteredByUserId)
        if enteredOnDateTime is None:
            enteredOnDateTime = datetime.datetime.now()
        dbPropertyType = self.propertyTypeHandler.getPropertyTypeByName(session, propertyTypeName)
        if (allowInternal == False and dbPropertyType.is_internal):
            raise CommandFailed("%s is an internal property. Can only be added and updated using specialized functionality." % propertyTypeName)
        else:
            allowedPropertyValueList = self.propertyTypeHandler.getAllowedPropertyTypeValuesById(session, dbPropertyType.id)
            self.propertyTypeHandler.checkPropertyValueIsAllowed(value, allowedPropertyValueList)
            dbPropertyValue = PropertyValue(tag=tag, value=value, display_value=displayValue,
                                            units=units, description=description,
                                            entered_on_date_time=enteredOnDateTime)
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

    def updatePropertyValueById(self, session, id, tag=None, value=None, units=None, description=None,
                                enteredByUserId=None, isDynamic=None, isUserWriteable=None):
        dbPropertyValue = self.findPropertyValueById(session, id)

        # Add Property value history
        self.addPropertyValueHistory(session, id,
                                     dbPropertyValue.tag,
                                     dbPropertyValue.value,
                                     dbPropertyValue.units,
                                     dbPropertyValue.description,
                                     dbPropertyValue.entered_by_user_id,
                                     dbPropertyValue.entered_on_date_time,
                                     dbPropertyValue.display_value,
                                     dbPropertyValue.target_value)

        enteredByDbUserInfo = self.userInfoHandler.getUserInfoById(session, enteredByUserId)
        enteredOnDateTime = datetime.datetime.now()
        allowedPropertyValueList = self.propertyTypeHandler.getAllowedPropertyTypeValuesById(session, dbPropertyValue.property_type_id)
        self.propertyTypeHandler.checkPropertyValueIsAllowed(value, allowedPropertyValueList)

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

    def getItemElementProperties(self, session, itemElementId, propertyTypeName = None):
        query = session.query(ItemElementProperty).join(PropertyValue)
        if propertyTypeName is not None:
            query = query.join(PropertyType).filter(PropertyType.name == propertyTypeName)
        query = query.filter(ItemElementProperty.item_element_id == itemElementId)
        dbItemElementProperties = query.all()
        return dbItemElementProperties

    def getPropertyValueListForItemElementId(self, session, itemElementId, propertyTypeName = None):
        entityDisplayName = self._getEntityDisplayName(PropertyValue)
        try:
            query = session.query(PropertyValue)\
                .join(ItemElementProperty)

            if propertyTypeName is not None:
                query = query.join(PropertyType).filter(PropertyType.name == propertyTypeName)

            query = query.filter(ItemElementProperty.item_element_id == itemElementId)

            dbPropertyValues = query.all()
            return dbPropertyValues
        except NoResultFound, ex:
            raise ObjectNotFound('No %s for item with id %s found.' % (entityDisplayName, id))

    def createUnverifiedPropertyValue(self, session, propertyTypeName, tag, value, units, description, enteredByUserId):
        enteredOnDateTime = datetime.datetime.now()
        dbPropertyType = self.propertyTypeHandler.getPropertyTypeByName(session, propertyTypeName)
        self.propertyTypeHandler.checkPropertyValueIsAllowed(value, dbPropertyType.allowedPropertyValueList)
        dbPropertyValue = PropertyValue(tag=tag, value=value, units=units, description=description, entered_by_user_id=enteredByUserId, entered_on_date_time=enteredOnDateTime)
        dbPropertyValue.propertyType = dbPropertyType
        return dbPropertyValue

