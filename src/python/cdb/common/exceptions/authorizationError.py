#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Authorization error class.
#

#######################################################################

from cdb.common.constants import cdbStatus 
from cdb.common.exceptions.cdbException import CdbException

#######################################################################

class AuthorizationError(CdbException):
    def __init__ (self, error='', **kwargs):
        CdbException.__init__(self, error, cdbStatus.CDB_AUTHORIZATION_ERROR, **kwargs)
