#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import componentInstanceLog

class ComponentInstanceLog(CdbDbEntity):

    cdbObjectClass = componentInstanceLog.ComponentInstanceLog

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


