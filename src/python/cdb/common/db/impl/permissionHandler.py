#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import datetime

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.db.impl.userInfoHandler import UserInfoHandler
from cdb.common.exceptions.invalidArgument import InvalidArgument
from cdb.common.exceptions.invalidSession import InvalidSession
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.itemElementProperty import ItemElementProperty
from cdb.common.db.entities.itemElement import ItemElement
from cdb.common.db.entities.userInfo import UserInfo
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler

class PermissionHandler(CdbDbEntityHandler):

    CDB_ADMIN_GROUP_NAME = 'CDB_ADMIN'

    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.userInfoHandler = UserInfoHandler()

    def verifyPermissionsForWriteToItemElement(self, session, userId, itemElementId = None, dbItemElementObject = None):
        if dbItemElementObject is None and itemElementId is None:
            raise InvalidArgument("At least the item element id or item element object must be provided.")
        if dbItemElementObject is None:
            dbItemElementObject = self._findDbObjById(session, ItemElement, itemElementId)
        else:
            itemElementId = dbItemElementObject.id
        dbUserInfo = self.userInfoHandler.getUserInfoById(session, userId)

        dbEntityInfo = dbItemElementObject.entityInfo
        ownerUserId = dbEntityInfo.ownerUserInfo.id

        if ownerUserId == userId:
            return True

        ownerGroupWriteable = dbEntityInfo.is_group_writeable
        ownerUserGroupId = dbEntityInfo.ownerUserGroup.id
        for userGroup in dbUserInfo.userGroupList:
            if ownerGroupWriteable:
                if ownerUserGroupId == userGroup.id:
                    return True
            if userGroup.name == self.CDB_ADMIN_GROUP_NAME:
                return True

        raise InvalidSession("User %s does not have permissions to modify item element %s" % (userId, itemElementId))

    def verifyPermissionsToUpdatePropertyValue(self, session, dbPropertyValue, userId):
        if dbPropertyValue.is_user_writeable:
            # Any user has permission to update.
            return True

        dbItemElementProperty = self.__getItemElementPropertyFromPropertyValueId(session, dbPropertyValue.id)
        dbItemElement = dbItemElementProperty.itemElement

        if self.verifyPermissionsForWriteToItemElement(session, userId, dbItemElementObject=dbItemElement):
            return True

        raise InvalidSession("User id %s  does not have necessary permissions to edit: %s" % (userId, dbPropertyValue))

    def __getItemElementPropertyFromPropertyValueId(self, session, propertyValueId):
        entityDisplayName = self._getEntityDisplayName(ItemElementProperty)
        try:
            query = session.query(ItemElementProperty).filter(ItemElementProperty.property_value_id == propertyValueId)
            dbItemElementProperty = query.one()
            return dbItemElementProperty
        except NoResultFound, ex:
            raise ObjectNotFound("No %s for property value with id %s found." % (entityDisplayName, propertyValueId))
