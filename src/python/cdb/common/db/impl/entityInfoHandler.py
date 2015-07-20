#!/usr/bin/env python

import datetime
from cdb.common.db.entities.entityInfo import EntityInfo
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler
from userInfoHandler import UserInfoHandler
from userGroupHandler import UserGroupHandler
from cdb.common.exceptions.authorizationError import AuthorizationError


class EntityInfoHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.userInfoHandler = UserInfoHandler()
        self.userGroupHandler = UserGroupHandler()

    @classmethod
    def checkEntityIsWriteable(cls, dbEntityInfo, dbUserInfo, adminGroupName=None):
        if dbUserInfo is None:
            raise AuthorizationError('User info has not been provided.');
        if adminGroupName is not None:
            for dbUserGroup in dbUserInfo.userGroupList:
                if dbUserGroup.name == adminGroupName:
                    # User belongs to admin group which can always edit entity
                    return

        if dbEntityInfo is None:
            raise AuthorizationError('Entity info has not been provided.');

        if dbEntityInfo.owner_user_id == dbUserInfo.id:
            # User owns this entity
            return

        if dbEntityInfo.is_group_writeable:
            # Entity is group writeable
            for dbUserGroup in dbUserInfo.userGroupList:
                if dbEntityInfo.owner_user_group_id == dbUserGroup.id:
                    # User belongs to group which can edit entity
                    return
        raise AuthorizationError('User %s is not authorized to modify this entity.' % (dbUserInfo.username));


    def createEntityInfo(self, session, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable):
        createdByDbUserInfo = self.userInfoHandler.getUserInfoById(session, createdByUserId)
        ownerDbUserInfo = self.userInfoHandler.getUserInfoById(session, ownerUserId)
        if ownerGroupId is not None:
            ownerDbUserGroup = self.userGroupHandler.getUserGroupById(session, ownerGroupId)
        createdOnDateTime = datetime.datetime.now()
        lastModifiedOnDateTime = createdOnDateTime 
        lastModifiedByUserId = createdByUserId
        lastModifiedByDbUserInfo = createdByDbUserInfo 
        
        dbEntityInfo = EntityInfo(created_on_date_time=createdOnDateTime, last_modified_on_date_time=lastModifiedOnDateTime, is_group_writeable=self.toIntegerFromBoolean(isGroupWriteable))
        dbEntityInfo.ownerUserInfo = ownerDbUserInfo 
        if ownerGroupId is not None:
            dbEntityInfo.ownerUserGroup = ownerDbUserGroup
        dbEntityInfo.createdByUserInfo = createdByDbUserInfo 
        dbEntityInfo.lastModifiedByUserInfo = lastModifiedByDbUserInfo 
        return dbEntityInfo

    def createUnverifiedEntityInfo(self, session, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable):
        createdOnDateTime = datetime.datetime.now()
        lastModifiedOnDateTime = createdOnDateTime 
        lastModifiedByUserId = createdByUserId
        dbEntityInfo = EntityInfo(created_by_user_id=createdByUserId, owner_user_id=ownerUserId, owner_user_group_id=ownerGroupId, created_on_date_time=createdOnDateTime, last_modified_by_user_id=lastModifiedByUserId, last_modified_on_date_time=lastModifiedOnDateTime, is_group_writeable=self.toIntegerFromBoolean(isGroupWriteable))
        return dbEntityInfo

