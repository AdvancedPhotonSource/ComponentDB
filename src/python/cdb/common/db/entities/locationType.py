#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import locationType

class LocationType(CdbDbEntity):

    cdbObjectClass = locationType.LocationType

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


