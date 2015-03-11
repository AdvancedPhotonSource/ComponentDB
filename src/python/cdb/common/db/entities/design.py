#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import design

class Design(CdbDbEntity):

    cdbObjectClass = design.Design

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


