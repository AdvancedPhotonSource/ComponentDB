#!/usr/bin/env python

from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.userGroup import UserGroup
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler

class UserGroupHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def getUserGroupList(self, session):
        self.logger.debug('Retrieving user group list')
        dbUserGroupList = session.query(UserGroup).all()
        return dbUserGroupList

