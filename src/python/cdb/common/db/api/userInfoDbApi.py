#!/usr/bin/env python

from cdb.common.exceptions.cdbException import CdbException
from cdb.common.db.api.cdbDbApi import CdbDbApi
from cdb.common.db.impl.userInfoHandler import UserInfoHandler

class UserInfoDbApi(CdbDbApi):

    def __init__(self):
        CdbDbApi.__init__(self)
        self.userInfoHandler = UserInfoHandler()

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


