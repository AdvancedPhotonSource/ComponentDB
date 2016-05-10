#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import designElementLog

class DesignElementLog(CdbDbEntity):

    cdbObjectClass = designElementLog.DesignElementLog

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


