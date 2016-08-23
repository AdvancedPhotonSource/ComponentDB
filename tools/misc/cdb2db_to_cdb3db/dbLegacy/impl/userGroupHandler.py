#!/usr/bin/env python

from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from dbLegacy.entities.userGroup import UserGroup
from dbLegacy.impl.cdbDbEntityHandler import CdbDbEntityHandler

class UserGroupHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def findUserGroupById(self, session, id):
        try:
            dbUserGroup = session.query(UserGroup).filter(UserGroup.id==id).one()
            return dbUserGroup
        except NoResultFound, ex:
            raise ObjectNotFound('User group id %s does not exist.' % (id))

    def findUserGroupByName(self, session, name):
        try:
            dbUserGroup = session.query(UserGroup).filter(UserGroup.name==name).one()
            return dbUserGroup
        except NoResultFound, ex:
            raise ObjectNotFound('User group %s does not exist.' % (name))

    def getUserGroups(self, session):
        self.logger.debug('Retrieving user group list')
        dbUserGroups = session.query(UserGroup).all()
        return dbUserGroups

    def getUserGroupById(self, session, id):
        try:
            self.logger.debug('Retrieving user group id %s' % id)
            dbUserGroup = session.query(UserGroup).filter(UserGroup.id==id).one()
            return dbUserGroup
        except NoResultFound, ex:
            raise ObjectNotFound('User group id %s does not exist.' % (id))

