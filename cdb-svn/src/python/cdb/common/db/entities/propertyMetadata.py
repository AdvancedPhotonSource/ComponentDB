#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import propertyMetadata

class PropertyMetadata(CdbDbEntity):

    entityDisplayName = 'property metadata'

    mappedColumnDict = { 
        'metadata_key' : 'metadataKey',
        'metadata_value' : 'metadataValue',
    }
    cdbObjectClass = propertyMetadata.PropertyMetadata

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


