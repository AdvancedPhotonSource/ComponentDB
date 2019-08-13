#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import logLevel


class LogLevel(CdbDbEntity):

    entityDisplayName = 'log level'

    cdbObjectClass = logLevel.LogLevel

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


