#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import log


class Log(CdbDbEntity):

    cdbObjectClass = log.Log

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


