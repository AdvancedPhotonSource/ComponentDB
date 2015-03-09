#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.userInfo import UserInfo
from cdb.common.db.entities.userGroup import UserGroup
from cdb.common.db.entities.userUserGroup import UserUserGroup
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler

class UserInfoHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

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

