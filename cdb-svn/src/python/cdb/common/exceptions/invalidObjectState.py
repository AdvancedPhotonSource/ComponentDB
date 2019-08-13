#!/usr/bin/env python

#
# Object not found error class.
#

#######################################################################

from cdb.common.constants import cdbStatus 
from cdb.common.exceptions.cdbException import CdbException

#######################################################################

class ObjectNotFound(CdbException):
    def __init__ (self, error='', **kwargs):
        CdbException.__init__(self, error, cdbStatus.CDB_INVALID_OBJECT_STATE, **kwargs)
