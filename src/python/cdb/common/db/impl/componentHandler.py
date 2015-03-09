#!/usr/bin/env python

import datetime
from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.componentType import ComponentType
from cdb.common.db.entities.component import Component
from cdb.common.db.entities.entityInfo import EntityInfo
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler
from componentTypeHandler import ComponentTypeHandler
from userInfoHandler import UserInfoHandler
from userGroupHandler import UserGroupHandler

class ComponentHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.componentTypeHandler = ComponentTypeHandler()
        self.userInfoHandler = UserInfoHandler()
        self.userGroupHandler = UserGroupHandler()

    def getComponents(self, session):
        self.logger.debug('Retrieving component list')
        dbComponents = session.query(Component).all()
        return dbComponents

    def getComponentById(self, session, id):
        try:
            self.logger.debug('Retrieving component id %s' % id)
            dbComponent = session.query(Component).filter(Component.id==id).one()
            return dbComponent
        except NoResultFound, ex:
            raise ObjectNotFound('Component id %s does not exist.' % (id))

    def addComponent(self, session, name, componentTypeId, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description=None):
        try:
            dbComponent = session.query(Component).filter(Component.name==name).one()
            raise ObjectAlreadyExists('Component %s already exists.' % name)
        except NoResultFound, ex:
            # OK.
            pass

        dbComponentType = self.componentTypeHandler.getComponentTypeById(session, componentTypeId)
        createdByDbUserInfo = self.userInfoHandler.getUserInfoById(session, createdByUserId)
        ownerDbUserInfo = self.userInfoHandler.getUserInfoById(session, ownerUserId)
        ownerDbUserGroup = self.userGroupHandler.getUserGroupById(session, ownerGroupId)
        createdOnDateTime = datetime.datetime.now()
        lastModifiedOnDateTime = createdOnDateTime 
        lastModifiedByUserId = createdByUserId
        lastModifiedByDbUserInfo = createdByDbUserInfo 
        
        # Create entity info
        #dbEntityInfo = EntityInfo(owner_user_id=ownerUserId, owner_user_group_id=ownerGroupId, created_by_user_id=createdByUserId, created_on_date_time=createdOnDateTime, last_modified_by_user_id=lastModifiedByUserId, last_modified_on_date_time=lastModifiedOnDateTime, is_group_writeable=isGroupWriteable)
        dbEntityInfo = EntityInfo(created_on_date_time=createdOnDateTime, last_modified_on_date_time=lastModifiedOnDateTime, is_group_writeable=isGroupWriteable)
        dbEntityInfo.ownerUserInfo = ownerDbUserInfo 
        dbEntityInfo.ownerUserGroup = ownerDbUserGroup
        dbEntityInfo.createdByUserInfo = createdByDbUserInfo 
        dbEntityInfo.lastModifiedByUserInfo = lastModifiedByDbUserInfo 
        #session.add(dbEntityInfo)
        #session.flush()

        #dbComponent = Component(name=name, component_type_id=dbComponentType.id, entity_info_id=dbEntityInfo.id, description=description)
        dbComponent = Component(name=name, component_type_id=dbComponentType.id, description=description)
        dbComponent.entityInfo = dbEntityInfo
        session.add(dbComponent)
        session.flush()
        self.logger.debug('Inserted component id %s' % dbComponent.id)
        print "DB COMPONENT IN HANDLER", dbComponent
        print "DB COMPONENT DICT IN HANDLER", dbComponent.__dict__
        return dbComponent

