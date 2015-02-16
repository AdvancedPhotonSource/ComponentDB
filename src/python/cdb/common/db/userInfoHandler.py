#!/usr/bin/env python

from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.userInfo import UserInfo
from cdb.common.db.cdbDbEntityHandler import CdbDbEntityHandler

class UserInfoHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def getUserInfo(self, session, username):
        try:
            self.logger.debug('Retrieving user info for %s' % username)
            dbUserInfo = session.query(UserInfo).filter(UserInfo.username==username).one()
            return dbUserInfo
        except NoResultFound, ex:
            raise ObjectNotFound('Username %s does not exist.' % (username))

