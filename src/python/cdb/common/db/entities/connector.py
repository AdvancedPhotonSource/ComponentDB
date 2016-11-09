#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import connector

class Connector(CdbDbEntity):

    entityDisplayName = 'connector'

    cdbObjectClass = connector.Connector

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


