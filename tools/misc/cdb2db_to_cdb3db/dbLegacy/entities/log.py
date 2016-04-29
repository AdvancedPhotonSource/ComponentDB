#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import log


class Log(CdbDbEntity):

    cdbObjectClass = log.Log

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


