#!/usr/bin/env python

from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.userGroup import UserGroup
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler

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

    def getUserGroupByName(self, session, name):
        self.logger.debug('Retrieving user group name %s' % name)
        return self.findUserGroupByName(session, name)

    def addGroup(self, session, groupName, description):
        self.logger.debug('Adding group %s' % groupName)
        try:
            dbGroupInfo = session.query(UserGroup).filter(UserGroup.name==groupName).one()
            raise ObjectAlreadyExists('Group %s already exists.' % (groupName))
        except NoResultFound, ex:
            # ok
            pass

        # Create group
        dbGroupInfo = UserGroup(name=groupName)
        if description:
            dbGroupInfo.description = description

        session.add(dbGroupInfo)
        session.flush()
        self.logger.debug('Inserted group id %s' % dbGroupInfo.id)
        return dbGroupInfo