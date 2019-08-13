#!/usr/bin/env python

import datetime
from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.exceptions.multipleObjectsFound import MultipleObjectsFound
from dbLegacy.entities.componentType import ComponentType
from dbLegacy.entities.component import Component
from dbLegacy.entities.componentProperty import ComponentProperty
from dbLegacy.entities.entityInfo import EntityInfo
from dbLegacy.impl.cdbDbEntityHandler import CdbDbEntityHandler
from componentTypeHandler import ComponentTypeHandler
from entityInfoHandler import EntityInfoHandler

class ComponentHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.componentTypeHandler = ComponentTypeHandler()
        self.entityInfoHandler = EntityInfoHandler()

    def findComponentById(self, session, id):
        try:
            dbComponent = session.query(Component).filter(Component.id==id).one()
            return dbComponent
        except NoResultFound, ex:
            raise ObjectNotFound('Component id %s does not exist.' % (id))

    def findComponentByModelNumber(self, session, modelNumber):
        try:
            dbComponent = session.query(Component).filter(Component.model_number==modelNumber).one()
            return dbComponent
        except NoResultFound, ex:
            raise ObjectNotFound('Component with model number %s does not exist.' % (modelNumber))

    def findComponentsByName(self, session, name):
        dbComponents = session.query(Component).filter(Component.name==name).all()
        return dbComponents

    def findComponentByName(self, session, name):
        dbComponents = self.findComponentsByName(session, name)
        if len(dbComponents) == 0:
            raise ObjectNotFound('Component with name %s does not exist.' % (name))
        elif len(dbComponents) == 1:
            return dbComponents[0]
        else:
            raise MultipleObjectsFound('There are multiple components with name %s.' % (name))

    # This method will not throw exception if both id and name are none
    def findComponentByIdOrName(self, session, id, name):
        if id is None and name is None:
            return None
        if id is not None:
            return self.findComponentById(session, id)
        return self.findComponentByName(session, name)

    # This method will not throw exception if both id and name are none
    def findComponentIdByIdOrName(self, session, id, name):
        dbComponent = self.findComponentByIdOrName(session, id, name)
        if dbComponent is None:
            return None
        return dbComponent.id

    def getComponents(self, session):
        self.logger.debug('Retrieving component list')
        dbComponents = session.query(Component).all()
        return dbComponents

    def getComponentsByName(self, session, name):
        self.logger.debug('Retrieving list of components with name %s' % (name))
        dbComponents = self.findComponentsByName(session, name)
        return dbComponents

    def getComponentById(self, session, id):
        self.logger.debug('Retrieving component id %s' % id)
        dbComponent = self.findComponentById(session, id)
        return dbComponent

    def getComponentByName(self, session, name):
        self.logger.debug('Retrieving component name %s' % name)
        dbComponent = self.findComponentByName(session, name)
        return dbComponent

    def getComponentByModelNumber(self, session, modelNumber):
        self.logger.debug('Retrieving component model number %s' % modelNumber)
        dbComponent = self.findComponentByModelNumber(session, modelNumber)
        return dbComponent

    def addComponent(self, session, name, modelNumber, componentTypeId, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description=None):
        try:
            if not modelNumber:
                dbComponent = self.findComponentByName(session, name)
                raise ObjectAlreadyExists('Component with name %s already exists.' % name)
            else:
                dbComponent = self.findComponentByModelNumber(session, modelNumber)
                raise ObjectAlreadyExists('Component with model number %s already exists.' % modelNumber)
        except ObjectNotFound, ex:
            # OK.
            pass

        dbComponentType = self.componentTypeHandler.getComponentTypeById(session, componentTypeId)
        # Create entity info
        dbEntityInfo = self.entityInfoHandler.createEntityInfo(session, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable)

        # Create component
        dbComponent = Component(name=name, model_number=modelNumber, description=description)
        dbComponent.componentType = dbComponentType
        dbComponent.entityInfo = dbEntityInfo
        session.add(dbComponent)
        session.flush()
        self.logger.debug('Inserted component id %s' % dbComponent.id)
        return dbComponent

