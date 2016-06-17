#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import allowedDomainHandlerEntityType

class AllowedDomainHandlerEntityType(CdbDbEntity):

    entityDisplayName = 'allowed domain handler entity type'

    cdbObjectClass = allowedDomainHandlerEntityType.AllowedDomainHandlerEntityType

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


