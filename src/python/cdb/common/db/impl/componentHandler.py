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
from entityInfoHandler import EntityInfoHandler

class ComponentHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.componentTypeHandler = ComponentTypeHandler()
        self.entityInfoHandler = EntityInfoHandler()

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

    def getComponentByName(self, session, name):
        try:
            self.logger.debug('Retrieving component name %s' % name)
            dbComponent = session.query(Component).filter(Component.name==name).one()
            return dbComponent
        except NoResultFound, ex:
            raise ObjectNotFound('Component with name %s does not exist.' % (name))

    def addComponent(self, session, name, componentTypeId, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description=None):
        try:
            dbComponent = session.query(Component).filter(Component.name==name).one()
            raise ObjectAlreadyExists('Component %s already exists.' % name)
        except NoResultFound, ex:
            # OK.
            pass
        dbComponentType = self.componentTypeHandler.getComponentTypeById(session, componentTypeId)
        # Create entity info
        dbEntityInfo = self.entityInfoHandler.createEntityInfo(session, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable)

        # Create component
        dbComponent = Component(name=name, description=description)
        dbComponent.componentType = dbComponentType
        dbComponent.entityInfo = dbEntityInfo
        session.add(dbComponent)
        session.flush()
        self.logger.debug('Inserted component id %s' % dbComponent.id)
        return dbComponent

