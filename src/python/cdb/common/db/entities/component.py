#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import component

class Component(CdbDbEntity):

    cdbObjectClass = component.Component

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


