#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import domainHandler

class DomainHandler(CdbDbEntity):

    entityDisplayName = 'domain handler'

    cdbObjectClass = domainHandler.DomainHandler

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


