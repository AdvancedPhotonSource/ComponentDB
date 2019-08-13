#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import locationType

class LocationType(CdbDbEntity):

    cdbObjectClass = locationType.LocationType

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


