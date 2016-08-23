#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import location

class Location(CdbDbEntity):

    cdbObjectClass = location.Location

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


