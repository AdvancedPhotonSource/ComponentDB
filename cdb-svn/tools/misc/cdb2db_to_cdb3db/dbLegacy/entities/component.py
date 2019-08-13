#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import component

class Component(CdbDbEntity):

    mappedColumnDict = {
        'model_number' : 'modelNumber',
    }
    cdbObjectClass = component.Component

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


