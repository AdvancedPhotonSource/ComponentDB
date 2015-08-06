#!/usr/bin/env python

from cdbObject import CdbObject
from cdb.common.exceptions.invalidArgument import InvalidArgument

class PdmLinkComponent(CdbObject):

    DEFAULT_COMPONENT_TYPE = 'Accelerator Component Temporary Assignment'
    PROPERTY_TYPE_PDM_NAME = 'PDMLink Drawing'
    PROPERTY_TYPE_WBS_NAME = 'WBS'

    DEFAULT_KEY_LIST = ['name', 'pdmPropertyValues', 'suggestedComponentTypes', 'wbsDescription']

    def __init__(self, dict):
        CdbObject.__init__(self, dict)
