#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Base CDB exception class.
#

#######################################################################

import exceptions
import json

from cdb.common.constants import cdbStatus 

#######################################################################

class CdbException(exceptions.Exception):
    """
    Base CDB exception class. 
                
    Usage:
        CdbException(errorMessage, errorCode)
        CdbException(args=errorMessage)
        CdbException(exception=exceptionObject)      
    """ 
    def __init__(self, error='', code=cdbStatus.CDB_ERROR, **kwargs):
        args = error
        if args == '':
            args = kwargs.get('args', '')
        ex = kwargs.get('exception', None)
        if ex != None:
            if isinstance(ex, exceptions.Exception):
                exArgs = '%s' % (ex)
            if args == '':
                args = exArgs
            else:
                args = "%s (%s)" % (args, exArgs)
        exceptions.Exception.__init__(self, args)
        self.code = code

    def getArgs(self):
        return self.args

    def getErrorCode(self):
        return self.code

    def getErrorMessage(self):
        return '%s' % (self.args)

    def getClassName(self):
        return '%s' % (self.__class__.__name__)
    
    def getExceptionType(self):
        return '%s' % (self.__class__.__name__).split('.')[-1]

    def getJsonRep(self):
        return json.dumps({
            'errorMessage'  : self.getErrorMessage(),
            'errorCode'     : self.getErrorCode(),
            'exceptionType' : self.getExceptionType(),
        })

    def getFullJsonRep(self):
        return self.getJsonRep();

