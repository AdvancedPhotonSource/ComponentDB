#!/usr/bin/env python

import datetime
from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.componentInstance import ComponentInstance
from cdb.common.db.entities.componentInstanceProperty import ComponentInstanceProperty
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler
from entityInfoHandler import EntityInfoHandler

class ComponentInstanceHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.entityInfoHandler = EntityInfoHandler()

    def findComponentInstanceById(self, session, id):
        try:
            dbComponentInstance = session.query(ComponentInstance).filter(ComponentInstance.id==id).one()
            return dbComponentInstance
        except NoResultFound, ex:
            raise ObjectNotFound('Component instance id %s does not exist.' % (id))

    def getComponentInstanceById(self, session, id):
        self.logger.debug('Retrieving component instance id %s' % id)
        dbComponentInstance = self.findComponentInstanceById(session, id)
        return dbComponentInstance

    def addComponentInstanceProperty(self, session, dbComponentInstance, dbPropertyValue):
        dbComponentInstanceProperty = ComponentInstanceProperty()
        dbComponentInstanceProperty.componentInstance = dbComponentInstance
        dbComponentInstanceProperty.propertyValue = dbPropertyValue
        session.add(dbComponentInstanceProperty)
        session.flush()
        return dbComponentInstanceProperty

