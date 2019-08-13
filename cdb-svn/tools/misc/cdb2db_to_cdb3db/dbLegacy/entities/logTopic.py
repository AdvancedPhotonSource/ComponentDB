#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import logTopic


class LogTopic(CdbDbEntity):

    cdbObjectClass = logTopic.LogTopic

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


