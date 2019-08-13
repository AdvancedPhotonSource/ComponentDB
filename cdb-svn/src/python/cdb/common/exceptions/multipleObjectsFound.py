#!/usr/bin/env python

#
# Multiple objects found error class.
#

#######################################################################

from cdb.common.constants import cdbStatus 
from cdb.common.exceptions.cdbException import CdbException

#######################################################################

class MultipleObjectsFound(CdbException):
    def __init__ (self, error='', **kwargs):
        CdbException.__init__(self, error, cdbStatus.CDB_MULTIPLE_OBJECTS_FOUND, **kwargs)
