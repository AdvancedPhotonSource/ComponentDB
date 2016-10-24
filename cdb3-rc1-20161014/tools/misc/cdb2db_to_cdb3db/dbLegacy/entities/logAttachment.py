#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import logAttachment


class LogAttachment(CdbDbEntity):

    cdbObjectClass = logAttachment.LogAttachment

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


