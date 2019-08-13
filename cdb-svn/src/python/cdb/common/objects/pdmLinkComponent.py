#!/usr/bin/env python

from cdbObject import CdbObject
from cdb.common.exceptions.invalidArgument import InvalidArgument

class PdmLinkComponent(CdbObject):

    PROPERTY_TYPE_PDM_NAME = 'PDMLink Drawing'
    PROPERTY_TYPE_WBS_NAME = 'WBS-DCC'

    DEFAULT_KEY_LIST = ['name', 'pdmPropertyValues', 'suggestedComponentTypes', 'wbsDescription', 'cdbDescription']

    def __init__(self, dict):
        CdbObject.__init__(self, dict)
