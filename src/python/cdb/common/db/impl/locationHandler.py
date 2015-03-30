#!/usr/bin/env python

import datetime
from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.locationType import LocationType
from cdb.common.db.entities.location import Location
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler
from locationTypeHandler import LocationTypeHandler

class LocationHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.locationTypeHandler = LocationTypeHandler()

    def getLocations(self, session):
        self.logger.debug('Retrieving location list')
        dbLocations = session.query(Location).all()
        return dbLocations

    def getLocationById(self, session, id):
        try:
            self.logger.debug('Retrieving location id %s' % id)
            dbLocation = session.query(Location).filter(Location.id==id).one()
            return dbLocation
        except NoResultFound, ex:
            raise ObjectNotFound('Location id %s does not exist.' % (id))

    def getLocationByName(self, session, name):
        try:
            self.logger.debug('Retrieving location name %s' % name)
            dbLocation = session.query(Location).filter(Location.name==name).one()
            return dbLocation
        except NoResultFound, ex:
            raise ObjectNotFound('Location with name %s does not exist.' % (name))

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

