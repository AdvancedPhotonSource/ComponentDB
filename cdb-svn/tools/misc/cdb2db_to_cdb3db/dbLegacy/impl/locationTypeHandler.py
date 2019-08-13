#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from dbLegacy.entities.locationType import LocationType
from dbLegacy.impl.cdbDbEntityHandler import CdbDbEntityHandler

class LocationTypeHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)

    def getLocationTypes(self, session):
        self.logger.debug('Retrieving location type list')
        dbLocationTypes = session.query(LocationType).all()
        return dbLocationTypes

    def getLocationTypeById(self, session, id):
        try:
            self.logger.debug('Retrieving location type id %s' % id)
            dbLocationType = session.query(LocationType).filter(LocationType.id==id).one()
            return dbLocationType
        except NoResultFound, ex:
            raise ObjectNotFound('Location type id %s does not exist.' % (id))


