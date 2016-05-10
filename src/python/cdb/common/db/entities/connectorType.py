#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import connectorType

class ConnectorType(CdbDbEntity):

    entityDisplayName = 'connector type'

    cdbObjectClass = connectorType.ConnectorType

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


