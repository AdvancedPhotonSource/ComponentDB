#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from dbLegacy.entities.componentInstanceLocationHistory import ComponentInstanceLocationHistory
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from dbLegacy.impl.cdbDbEntityHandler import CdbDbEntityHandler


class ComponentInstanceLocationHistoryHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def findComponentInstanceLocationHistoryById(self, session, id):
        try:
            dbComponentInstanceLocationHistory = session.query(ComponentInstanceLocationHistory).filter(ComponentInstanceLocationHistory.id==id).one()
            return dbComponentInstanceLocationHistory
        except NoResultFound, ex:
            raise ObjectNotFound('Component instance location history id %s does not exist.' % (id))

    def findComponentLocationHistoryByComponentInstanceId(self, session, componentInstanceId):
        try:
            dbComponentInstanceLocationHistory = session.query(ComponentInstanceLocationHistory).filter(ComponentInstanceLocationHistory.component_instance_id==componentInstanceId).all()
            return dbComponentInstanceLocationHistory
        except NoResultFound, ex:
            raise ObjectNotFound('Component instance location history for component instance with id %s does not exist.' % (id))

    def getComponentInstanceLocationHistoryById(self, session, id):
        self.logger.debug('Retrieving component instance location history id %s' % id)
        dbComponentInstanceLocationHistory = self.findComponentInstanceLocationHistoryById(session, id)
        return dbComponentInstanceLocationHistory

    def getComponentInstanceLocationHistoryByComponentInstanceId(self, session, componentInstanceId):
        self.logger.debug('Retrieving component instance location history for component instance id %s' % id)
        dbComponentInstanceLocationHistory = self.findComponentLocationHistoryByComponentInstanceId(session, componentInstanceId)
        return dbComponentInstanceLocationHistory

    def addComponentInstanceLocationHistory(self, session, dbComponentInstance):
        if not dbComponentInstance.location:
            raise ObjectNotFound('Component Instance has no past locations to add.')


        location = dbComponentInstance.location
        locationDetails = dbComponentInstance.location_details
        dbLastModifiedUserInfo = dbComponentInstance.entityInfo.lastModifiedByUserInfo
        lastMofiedOnDateTime =  dbComponentInstance.entityInfo.last_modified_on_date_time

        self.logger.debug('Adding component instance location history')
        dbComponentInstanceLocationHistory = ComponentInstanceLocationHistory()
        dbComponentInstanceLocationHistory.location = location
        dbComponentInstanceLocationHistory.location_details = locationDetails
        dbComponentInstanceLocationHistory.userInfo = dbLastModifiedUserInfo
        dbComponentInstanceLocationHistory.entered_on_date_time = lastMofiedOnDateTime
        dbComponentInstanceLocationHistory.componentInstance = dbComponentInstance

        session.add(dbComponentInstanceLocationHistory)
        session.flush()
        return dbComponentInstanceLocationHistory
