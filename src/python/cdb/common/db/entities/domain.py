#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import domain

class Domain(CdbDbEntity):

    cdbObjectClass = domain.Domain

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


