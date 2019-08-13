#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import source

class Source(CdbDbEntity):

    cdbObjectClass = source.Source

    mappedColumnDict = {
        'contact_info' : 'contactInfo'
    }

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


