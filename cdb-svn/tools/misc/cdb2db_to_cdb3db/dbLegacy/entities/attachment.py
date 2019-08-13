#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import attachment


class Attachment(CdbDbEntity):

    cdbObjectClass = attachment.Attachment

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


