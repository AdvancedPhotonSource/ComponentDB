#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from dbLegacy.entities.userInfo import UserInfo
from dbLegacy.entities.userGroup import UserGroup
from dbLegacy.entities.userUserGroup import UserUserGroup
from dbLegacy.impl.cdbDbEntityHandler import CdbDbEntityHandler
from dbLegacy.impl.userGroupHandler import UserGroupHandler

class UserInfoHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def findUserInfoById(self, session, id):
        try:
            dbUserInfo = session.query(UserInfo).filter(UserInfo.id==id).one()
            return dbUserInfo 
        except NoResultFound, ex:
            raise ObjectNotFound('User id %s does not exist.' % (id))

    def findUserInfoByUsername(self, session, username):
        try:
            dbUserInfo = session.query(UserInfo).filter(UserInfo.username==username).one()
            return dbUserInfo 
        except NoResultFound, ex:
            raise ObjectNotFound('User %s does not exist.' % (username))

    def getUserInfos(self, session):
        self.logger.debug('Retrieving user info list')
        dbUserInfos = session.query(UserInfo).all()
        for dbUserInfo in dbUserInfos:
            dbUserGroups = session.query(UserGroup).join(UserUserGroup).filter(and_(UserUserGroup.user_id==dbUserInfo.id, UserUserGroup.user_group_id==UserGroup.id)).all()
            dbUserInfo.userGroupList = dbUserGroups
            # Remove password
            del dbUserInfo.password
        return dbUserInfos

    def getUserInfoById(self, session, id):
        try:
            self.logger.debug('Retrieving user id %s' % id)
            dbUserInfo = session.query(UserInfo).filter(UserInfo.id==id).one()
            dbUserGroups = session.query(UserGroup).join(UserUserGroup).filter(and_(UserUserGroup.user_id==dbUserInfo.id, UserUserGroup.user_group_id==UserGroup.id)).all()
            dbUserInfo.userGroupList = dbUserGroups
            # Remove password
            del dbUserInfo.password
            return dbUserInfo
        except NoResultFound, ex:
            raise ObjectNotFound('User id %s does not exist.' % (id))

    def getUserInfoByUsername(self, session, username):
        try:
            self.logger.debug('Retrieving user %s' % username)
            dbUserInfo = session.query(UserInfo).filter(UserInfo.username==username).one()
            dbUserGroups = session.query(UserGroup).join(UserUserGroup).filter(and_(UserUserGroup.user_id==dbUserInfo.id, UserUserGroup.user_group_id==UserGroup.id)).all()
            dbUserInfo.userGroupList = dbUserGroups
            # Remove password
            del dbUserInfo.password
            return dbUserInfo
        except NoResultFound, ex:
            raise ObjectNotFound('Username %s does not exist.' % (username))

    def getUserInfoWithPasswordByUsername(self, session, username):
        try:
            self.logger.debug('Retrieving user %s (with password)' % username)
            dbUserInfo = session.query(UserInfo).filter(UserInfo.username==username).one()
            dbUserGroups = session.query(UserGroup).join(UserUserGroup).filter(and_(UserUserGroup.user_id==dbUserInfo.id, UserUserGroup.user_group_id==UserGroup.id)).all()
            dbUserInfo.userGroupList = dbUserGroups
            return dbUserInfo
        except NoResultFound, ex:
            raise ObjectNotFound('Username %s does not exist.' % (username))

    def addUser(self, session, username, firstName, lastName, middleName, email, description, password):
        self.logger.debug('Adding user %s' % username)
        try:
            dbUserInfo = session.query(UserInfo).filter(UserInfo.username==username).one()
            raise ObjectAlreadyExists('User %s already exists.' % (username))
        except NoResultFound, ex:
            # ok
            pass

        # Create user
        dbUserInfo = UserInfo(username=username, first_name=firstName, last_name=lastName)
        if middleName:
            dbUserInfo.middle_name = middleName
        if email:
            dbUserInfo.email = email
        if password:
            dbUserInfo.password = password
        if description:
            dbUserInfo.description = description
        session.add(dbUserInfo)
        session.flush()
        self.logger.debug('Inserted user id %s' % dbUserInfo.id)
        return dbUserInfo

    def addUserToGroup(self, session, username, groupName):
        dbUserInfo = self.findUserInfoByUsername(session, username)
        userGroupHandler = UserGroupHandler()
        dbUserGroup = userGroupHandler.findUserGroupByName(session, groupName)
        dbUserGroups = session.query(UserGroup).join(UserUserGroup).filter(and_(UserUserGroup.user_id==dbUserInfo.id, UserUserGroup.user_group_id==UserGroup.id)).all()
        for g in dbUserGroups:
            if g.id == dbUserGroup.id:
                raise ObjectAlreadyExists('User %s is already a member of group %s.' % (username,groupName))
        dbUserUserGroup = UserUserGroup(user_id=dbUserInfo.id, user_group_id=dbUserGroup.id)
        session.add(dbUserUserGroup)
        session.flush()
        return dbUserUserGroup



