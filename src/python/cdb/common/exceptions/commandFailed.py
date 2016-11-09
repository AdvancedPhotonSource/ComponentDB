#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Command failed exception class.
#

#######################################################################

from cdb.common.constants import cdbStatus 
from cdb.common.exceptions.cdbException import CdbException

#######################################################################

class CommandFailed(CdbException):
    def __init__ (self, error='', **kwargs):
        CdbException.__init__(self, error, cdbStatus.CDB_COMMAND_FAILED, **kwargs)
