#!/usr/bin/env python

#
# External Service Error class.
#

#######################################################################

from cdb.common.constants import cdbStatus 
from cdb.common.exceptions.cdbException import CdbException

#######################################################################

class ExternalServiceError(CdbException):
    def __init__ (self, error='', **kwargs):
        CdbException.__init__(self, error, cdbStatus.CDB_EXTERNAL_SERVICE_ERROR, **kwargs)
