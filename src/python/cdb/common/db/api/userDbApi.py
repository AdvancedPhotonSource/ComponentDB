#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.exceptions.cdbException import CdbException
from cdb.common.db.api.cdbDbApi import CdbDbApi
from cdb.common.db.impl.userInfoHandler import UserInfoHandler
from cdb.common.db.impl.userGroupHandler import UserGroupHandler

class UserDbApi(CdbDbApi):

    def __init__(self):
        CdbDbApi.__init__(self)
        self.userInfoHandler = UserInfoHandler()
        self.userGroupHandler = UserGroupHandler()

    @CdbDbApi.executeQuery
    def getUserGroups(self, **kwargs):
        """
        Get all user group records.

        :param kwargs:
        :return: CdbObject List of resulting records.
        """
        session = kwargs['session']
        dbUserGroups = self.userGroupHandler.getUserGroups(session)
        return self.toCdbObjectList(dbUserGroups)

    @CdbDbApi.executeQuery
    def getUserGroupByName(self, name, **kwargs):
        """
        Get a user group record by its name.

        :param name:
        :param kwargs:
        :return: (CdbObject) resulting record.
        """
        session = kwargs['session']
        dbUserGroup = self.userGroupHandler.getUserGroupByName(session, name)
        return dbUserGroup.getCdbObject()

    @CdbDbApi.executeQuery
    def getUsers(self, **kwargs):
        """
        Get all user records.

        :param kwargs:
        :return: CdbObject List of resulting records.
        """
        session = kwargs['session']
        dbUsers = self.userInfoHandler.getUserInfos(session)
        return self.toCdbObjectList(dbUsers)

    @CdbDbApi.executeQuery
    def getUserById(self, id, **kwargs):
        """
        Get a user record by its id.

        :param id:
        :param kwargs:
        :return: (CdbObject) resulting record.
        """
        session = kwargs['session']
        dbUserInfo = self.userInfoHandler.getUserInfoById(session, id)
        return dbUserInfo.getCdbObject()

    @CdbDbApi.executeQuery
    def getUserByUsername(self, username, **kwargs):
        """
        Get a user record by its username attribute.

        :param username:
        :param kwargs:
        :return: (CdbObject) resulting record.
        """
        session = kwargs['session']
        dbUserInfo = self.userInfoHandler.getUserInfoByUsername(session, username)
        return dbUserInfo.getCdbObject()

    @CdbDbApi.executeTransaction
    def addUser(self, username, firstName, lastName, middleName, email, description, password, **kwargs):
        """
        Add a new user record.

        :param username:
        :param firstName:
        :param lastName:
        :param middleName:
        :param email:
        :param description:
        :param password:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbUserInfo = self.userInfoHandler.addUser(session, username, firstName, lastName, middleName, email, description, password)
        return dbUserInfo.getCdbObject()

    @CdbDbApi.executeTransaction
    def addUserToGroup(self, username, groupName, **kwargs):
        """
        Assign a user to a particular group.

        :param username:
        :param groupName:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbUserUserGroup = self.userInfoHandler.addUserToGroup(session, username, groupName)
        return dbUserUserGroup.getCdbObject()

    @CdbDbApi.executeTransaction
    def addGroup(self, name, description, **kwargs):
        """
        add a new user group record.

        :param name:
        :param description:
        :param kwargs:
        :return: (CdbObject) newly added record.
        """
        session = kwargs['session']
        dbUserUserGroup = self.userGroupHandler.addGroup(session, name, description)
        return dbUserUserGroup.getCdbObject()

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

    print api.addUserToGroup('sv', 'CDB_ADMIN')
