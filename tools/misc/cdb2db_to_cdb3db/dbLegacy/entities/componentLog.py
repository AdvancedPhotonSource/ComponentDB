#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import componentLog

class ComponentLog(CdbDbEntity):

    cdbObjectClass = componentLog.ComponentLog

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


