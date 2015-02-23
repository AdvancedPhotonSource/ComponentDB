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

    def getUserInfo(self, session, username):
        try:
            self.logger.debug('Retrieving user info for %s' % username)
            dbUserInfo = session.query(UserInfo).filter(UserInfo.username==username).one()
            dbUserGroupList = session.query(UserGroup).join(UserUserGroup).filter(and_(UserUserGroup.user_id==dbUserInfo.id, UserUserGroup.user_group_id==UserGroup.id)).all()
            dbUserInfo.userGroupList = dbUserGroupList
            return dbUserInfo
        except NoResultFound, ex:
            raise ObjectNotFound('Username %s does not exist.' % (username))

