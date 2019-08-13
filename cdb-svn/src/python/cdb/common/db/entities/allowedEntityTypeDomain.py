#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import allowedEntityTypeDomain

class AllowedEntityTypeDomain(CdbDbEntity):

    entityDisplayName = 'allowed entity type domain'

    cdbObjectClass = allowedEntityTypeDomain.AllowedEntityTypeDomain

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


