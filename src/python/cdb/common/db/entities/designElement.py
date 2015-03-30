#!/usr/bin/env python

from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import designElement

class DesignElement(CdbDbEntity):

    mappedColumnDict = {
        'parent_design_id' : 'parentDesignId',
        'parent_design_element_id' : 'parentDesignElementId',
        'child_design_id' : 'childDesignId',
        'component_id' : 'componentId',
        'location_id' : 'locationId',
        'sort_order' : 'sortOrder',
    }
    cdbObjectClass = designElement.DesignElement

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


