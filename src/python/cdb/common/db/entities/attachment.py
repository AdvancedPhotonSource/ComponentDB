#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import attachment


class Attachment(CdbDbEntity):

    cdbObjectClass = attachment.Attachment

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


