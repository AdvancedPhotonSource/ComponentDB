#!/usr/bin/env python

from cdb.common.exceptions.cdbException import CdbException
from cdb.common.db.api.cdbDbApi import CdbDbApi
from cdb.common.db.impl.userInfoHandler import UserInfoHandler
from cdb.common.db.impl.userGroupHandler import UserGroupHandler

class UserInfoDbApi(CdbDbApi):

    def __init__(self):
        CdbDbApi.__init__(self)
        self.userInfoHandler = UserInfoHandler()
        self.userGroupHandler = UserGroupHandler()

    def getUserGroupList(self):
        try:
            session = self.dbManager.openSession()
            try:
                dbUserGroupList = self.userGroupHandler.getUserGroupList(session)
                return self.toCdbObjectList(dbUserGroupList)
            except CdbException, ex:
                raise
            except Exception, ex:
                self.logger.exception('%s' % ex)
                raise
        finally:
            self.dbManager.closeSession(session)

    def getUserInfoList(self):
        try:
            session = self.dbManager.openSession()
            try:
                dbUserInfoList = self.userInfoHandler.getUserInfoList(session)
                return self.toCdbObjectList(dbUserInfoList)
            except CdbException, ex:
                raise
            except Exception, ex:
                self.logger.exception('%s' % ex)
                raise
        finally:
            self.dbManager.closeSession(session)

    def getUserInfoById(self, id):
        try:
            session = self.dbManager.openSession()
            try:
                dbUserInfo = self.userInfoHandler.getUserInfoById(session, id)
                return dbUserInfo.getCdbObject()
            except CdbException, ex:
                raise
            except Exception, ex:
                self.logger.exception('%s' % ex)
                raise
        finally:
            self.dbManager.closeSession(session)

    def getUserInfoByUsername(self, username):
        try:
            session = self.dbManager.openSession()
            try:
                dbUserInfo = self.userInfoHandler.getUserInfoByUsername(session, username)
                return dbUserInfo.getCdbObject()
            except CdbException, ex:
                raise
            except Exception, ex:
                self.logger.exception('%s' % ex)
                raise
        finally:
            self.dbManager.closeSession(session)

#######################################################################
# Testing.
if __name__ == '__main__':
    api = UserInfoDbApi()
    userInfo = api.getUserInfoByUsername('sveseli')
    print userInfo
    userInfoList = api.getUserInfoList()
    for userInfo in userInfoList:
        print userInfo.getJsonRep()

    print 
    print 'User Groups'
    print '***********'
    userGroupList = api.getUserGroupList()
    for userGroup in userGroupList:
        print userGroup.getDictRep()

