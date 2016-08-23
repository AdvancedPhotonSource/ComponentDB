#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import logTopic


class LogTopic(CdbDbEntity):

    entityDisplayName = 'log topic'

    cdbObjectClass = logTopic.LogTopic

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


