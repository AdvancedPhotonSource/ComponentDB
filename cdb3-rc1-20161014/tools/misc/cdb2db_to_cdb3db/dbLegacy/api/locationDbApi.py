#!/usr/bin/env python

from cdb.common.exceptions.cdbException import CdbException
from dbLegacy.api.cdbDbApi import CdbDbApi
from dbLegacy.impl.locationHandler import LocationHandler
from dbLegacy.impl.locationTypeHandler import LocationTypeHandler

class LocationDbApi(CdbDbApi):

    def __init__(self):
        CdbDbApi.__init__(self)
        self.locationTypeHandler = LocationTypeHandler()
        self.locationHandler = LocationHandler()

    @CdbDbApi.executeQuery
    def getLocationTypes(self, **kwargs):
        session = kwargs['session']
        dbLocationTypes = self.locationTypeHandler.getLocationTypes(session)
        return self.toCdbObjectList(dbLocationTypes)

    @CdbDbApi.executeQuery
    def getLocations(self, **kwargs):
        session = kwargs['session']
        dbLocations = self.locationHandler.getLocations(session)
        return self.toCdbObjectList(dbLocations)

    @CdbDbApi.executeQuery
    def getLocationLinks(self, **kwargs):
        session = kwargs['session']
        dbLocations = self.locationHandler.getLocationLinks(session)
        return self.toCdbObjectList(dbLocations)

    @CdbDbApi.executeQuery
    def getChildLocations(self, parentLocationId, **kwargs):
        session = kwargs['session']
        dbLocations = self.locationHandler.getLocationLinksForParentId(session, parentLocationId)
        return self.toCdbObjectList(dbLocations)

    @CdbDbApi.executeQuery
    def getLocationById(self, id, **kwargs):
        session = kwargs['session']
        dbLocation = self.locationHandler.getLocationById(session, id)
        return dbLocation.getCdbObject()

    @CdbDbApi.executeQuery
    def getLocationByName(self, name, **kwargs):
        session = kwargs['session']
        dbLocation = self.locationHandler.getLocationByName(session, name)
        return dbLocation.getCdbObject()

    @CdbDbApi.executeTransaction
    def addLocation(self, name, locationTypeId, isUserWriteable, sortOrder, description, **kwargs):
        session = kwargs['session']
        dbLocation = self.locationHandler.addLocation(session, name, locationTypeId, isUserWriteable, sortOrder, description)
        return dbLocation.getCdbObject()

#######################################################################
# Testing.
if __name__ == '__main__':
    api = LocationDbApi()

    locationTypes = api.getLocationTypes()
    for locationType in locationTypes:
        print locationType

    locations = api.getLocations()
    for location in locations:
        print
        print "********************"
        print location
        print "TEXT"
        print location.getTextRep()
        print "DICT"
        print location.getDictRep()
        print "JSON"
        print location.getJsonRep()

    print 'Getting location'
    location = api.getLocationById(10)
    print location.getDictRep()

    print 'Adding location'
    location = api.addLocation(name='acd8', locationTypeId=3, isUserWriteable=True, sortOrder=3.3, description='Test Location')
    print "Added Location"
    print location
