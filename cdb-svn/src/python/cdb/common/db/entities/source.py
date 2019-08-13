#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import source

class Source(CdbDbEntity):

    cdbObjectClass = source.Source

    mappedColumnDict = {
        'contact_info' : 'contactInfo'
    }

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


