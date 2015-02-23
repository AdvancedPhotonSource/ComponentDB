#!/usr/bin/env python

from cdb.common.exceptions.cdbException import CdbException
from cdb.common.db.api.cdbDbApi import CdbDbApi
from cdb.common.db.impl.userInfoHandler import UserInfoHandler

class UserInfoDbApi(CdbDbApi):

    def __init__(self):
        CdbDbApi.__init__(self)
        self.userInfoHandler = UserInfoHandler()

    def getUserInfo(self, username):
        try:
            session = self.dbManager.openSession()
            try:
                dbUserInfo = self.userInfoHandler.getUserInfo(session, username)
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
    dbUserInfo = api.getUserInfo('sveseli')
    print dbUserInfo
