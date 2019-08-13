#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import designLog

class DesignLog(CdbDbEntity):

    cdbObjectClass = designLog.DesignLog

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


