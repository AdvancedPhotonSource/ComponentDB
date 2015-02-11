#!/usr/bin/env python

#
# Invalid argument error class.
#

#######################################################################

from cdb.common.constants import cdbStatus 
from cdb.common.exceptions.cdbException import CdbException

#######################################################################

class InvalidArgument(CdbException):
    def __init__ (self, error='', **kwargs):
        CdbException.__init__(self, error, cdbStatus.CDB_INVALID_ARGUMENT_ERROR, **kwargs)
