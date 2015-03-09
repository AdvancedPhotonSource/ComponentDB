#!/usr/bin/env python

from cdb.common.exceptions.cdbException import CdbException
from cdb.common.db.api.cdbDbApi import CdbDbApi
from cdb.common.db.impl.userInfoHandler import UserInfoHandler
from cdb.common.db.impl.userGroupHandler import UserGroupHandler

class UserDbApi(CdbDbApi):

    def __init__(self):
        CdbDbApi.__init__(self)
        self.userInfoHandler = UserInfoHandler()
        self.userGroupHandler = UserGroupHandler()

    def getUserGroups(self):
        try:
            session = self.dbManager.openSession()
            try:
                dbUserGroups = self.userGroupHandler.getUserGroups(session)
                return self.toCdbObjectList(dbUserGroups)
            except CdbException, ex:
                raise
            except Exception, ex:
                self.logger.exception('%s' % ex)
                raise
        finally:
            self.dbManager.closeSession(session)

    def getUsers(self):
        try:
            session = self.dbManager.openSession()
            try:
                dbUsers = self.userInfoHandler.getUserInfos(session)
                return self.toCdbObjectList(dbUsers)
            except CdbException, ex:
                raise
            except Exception, ex:
                self.logger.exception('%s' % ex)
                raise
        finally:
            self.dbManager.closeSession(session)

    def getUserById(self, id):
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

    def getUserByUsername(self, username):
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

    def getUserWithPasswordByUsername(self, username):
        try:
            session = self.dbManager.openSession()
            try:
                dbUserInfo = self.userInfoHandler.getUserInfoWithPasswordByUsername(session, username)
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
    api = UserDbApi()
    user = api.getUserByUsername('sveseli')
    print user
    user = api.getUserWithPasswordByUsername('sveseli')
    print user
    users = api.getUsers()
    for user in users:
        print user.getDictRep()
        print user.__dict__

    print 
    print 'User Groups'
    print '***********'
    userGroups = api.getUserGroups()
    for userGroup in userGroups:
        print userGroup.getDictRep()

