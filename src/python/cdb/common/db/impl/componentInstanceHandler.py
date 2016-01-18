#!/usr/bin/env python

import datetime
from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.componentInstance import ComponentInstance
from cdb.common.db.entities.componentInstanceProperty import ComponentInstanceProperty
from cdb.common.db.impl.componentInstanceLocationHistoryHandler import ComponentInstanceLocationHistoryHandler
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler
from cdb.common.db.impl.locationHandler import LocationHandler
from cdb.common.db.impl.entityInfoHandler import EntityInfoHandler


class ComponentInstanceHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.entityInfoHandler = EntityInfoHandler()
        self.componentInstanceLocationHistoryHandler = ComponentInstanceLocationHistoryHandler()
        self.locationHandler = LocationHandler()

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

    def updateComponentInstanceLocation(self, session, componentInstanceId, locationId, locationDetails, enteredByUserId):
        self.logger.debug('Updating location for component instance id %s' % componentInstanceId)
        dbComponentInstance = self.getComponentInstanceById(session, componentInstanceId)

        if dbComponentInstance.location:
            self.componentInstanceLocationHistoryHandler.addComponentInstanceLocationHistory(session, dbComponentInstance)

        dbLocation = self.locationHandler.getLocationById(session, locationId)
        dbComponentInstance.location = dbLocation
        dbComponentInstance.location_details = locationDetails

        # Update entity information
        dbEntityInfo = dbComponentInstance.entityInfo
        dbComponentInstance.entityInfo = self.entityInfoHandler.updateEntityInfo(session, dbEntityInfo, enteredByUserId)

        session.add(dbComponentInstance)
        session.flush()
        return dbComponentInstance


