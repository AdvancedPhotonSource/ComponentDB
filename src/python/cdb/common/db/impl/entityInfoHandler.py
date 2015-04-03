#!/usr/bin/env python

import datetime
from cdb.common.db.entities.entityInfo import EntityInfo
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler
from userInfoHandler import UserInfoHandler
from userGroupHandler import UserGroupHandler


class EntityInfoHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.userInfoHandler = UserInfoHandler()
        self.userGroupHandler = UserGroupHandler()


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

