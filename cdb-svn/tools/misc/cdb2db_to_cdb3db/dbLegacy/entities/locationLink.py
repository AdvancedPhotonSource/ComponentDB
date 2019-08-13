#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import locationLink

class LocationLink(CdbDbEntity):

    cdbObjectClass = locationLink.LocationLink

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


