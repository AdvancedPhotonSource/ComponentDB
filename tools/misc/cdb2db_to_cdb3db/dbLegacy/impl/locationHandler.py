#!/usr/bin/env python

import datetime
from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from dbLegacy.entities.locationType import LocationType
from dbLegacy.entities.location import Location
from dbLegacy.impl.cdbDbEntityHandler import CdbDbEntityHandler
from locationTypeHandler import LocationTypeHandler

class LocationHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.locationTypeHandler = LocationTypeHandler()

    def findLocationById(self, session, id):
        try:
            dbLocation = session.query(Location).filter(Location.id==id).one()
            return dbLocation
        except NoResultFound, ex:
            raise ObjectNotFound('Location id %s does not exist.' % (id))

    def findLocationByName(self, session, name):
        try:
            dbLocation = session.query(Location).filter(Location.name==name).one()
            return dbLocation
        except NoResultFound, ex:
            raise ObjectNotFound('Location with name %s does not exist.' % (name))

    # This method will not throw exception if both id and name are none
    def findLocationByIdOrName(self, session, id, name):
        if id is None and name is None:
            return None
        if id is not None:
            return self.findLocationById(session, id)
        return self.findLocationByName(session, name)

    # This method will not throw exception if both id and name are none
    def findLocationIdByIdOrName(self, session, id, name):
        dbLocation = self.findLocationByIdOrName(session, id, name)
        if dbLocation is None:
            return None
        return dbLocation.id

    def getLocations(self, session):
        self.logger.debug('Retrieving location list')
        dbLocations = session.query(Location).all()
        return dbLocations

    def getLocationById(self, session, id):
        self.logger.debug('Retrieving location id %s' % id)
        dbLocation =self.findLocationById(session, id) 
        return dbLocation

    def getLocationByName(self, session, name):
        self.logger.debug('Retrieving location name %s' % name)
        dbLocation = self.findLocationByName(session, name)
        return dbLocation

    def addLocation(self, session, name, locationTypeId, isUserWriteable, sortOrder, description=None):
        try:
            dbLocation = session.query(Location).filter(Location.name==name).one()
            raise ObjectAlreadyExists('Location %s already exists.' % name)
        except NoResultFound, ex:
            # OK.
            pass
        dbLocationType = self.locationTypeHandler.getLocationTypeById(session, locationTypeId)

        # Create location
        dbLocation = Location(name=name, is_user_writeable=isUserWriteable, sort_order=sortOrder, description=description)
        dbLocation.locationType = dbLocationType
        session.add(dbLocation)
        session.flush()
        self.logger.debug('Inserted location id %s' % dbLocation.id)
        return dbLocation

