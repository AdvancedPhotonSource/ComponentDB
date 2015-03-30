#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import location

class Location(CdbDbEntity):

    cdbObjectClass = location.Location

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


