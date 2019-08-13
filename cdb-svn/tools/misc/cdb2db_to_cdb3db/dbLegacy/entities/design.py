#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import design

class Design(CdbDbEntity):

    cdbObjectClass = design.Design

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


