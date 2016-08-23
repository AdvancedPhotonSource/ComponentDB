#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import componentSource

class ComponentSource(CdbDbEntity):

    cdbObjectClass = componentSource.ComponentSource

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


