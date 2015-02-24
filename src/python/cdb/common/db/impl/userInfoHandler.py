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

    def getUserInfoList(self, session):
        self.logger.debug('Retrieving user info list')
        dbUserInfoList = session.query(UserInfo).all()
        for dbUserInfo in dbUserInfoList:
            dbUserGroupNameList = session.query(UserGroup.name).join(UserUserGroup).filter(and_(UserUserGroup.user_id==dbUserInfo.id, UserUserGroup.user_group_id==UserGroup.id)).all()
            dbUserInfo.userGroupNameList = map(lambda x: x[0], dbUserGroupNameList)
        return dbUserInfoList

    def getUserInfoById(self, session, id):
        try:
            self.logger.debug('Retrieving user info for id %s' % id)
            dbUserInfo = session.query(UserInfo).filter(UserInfo.id==id).one()
            dbUserGroupNameList = session.query(UserGroup.name).join(UserUserGroup).filter(and_(UserUserGroup.user_id==dbUserInfo.id, UserUserGroup.user_group_id==UserGroup.id)).all()
            dbUserInfo.userGroupNameList = map(lambda x: x[0], dbUserGroupNameList)
            return dbUserInfo
        except NoResultFound, ex:
            raise ObjectNotFound('User id %s does not exist.' % (id))

    def getUserInfoByUsername(self, session, username):
        try:
            self.logger.debug('Retrieving user info for %s' % username)
            dbUserInfo = session.query(UserInfo).filter(UserInfo.username==username).one()
            dbUserGroupNameList = session.query(UserGroup.name).join(UserUserGroup).filter(and_(UserUserGroup.user_id==dbUserInfo.id, UserUserGroup.user_group_id==UserGroup.id)).all()
            dbUserInfo.userGroupNameList = map(lambda x: x[0], dbUserGroupNameList)
            return dbUserInfo
        except NoResultFound, ex:
            raise ObjectNotFound('Username %s does not exist.' % (username))

