#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.constants import cdbStatus
from cdb.common.exceptions import cdbExceptionMap
from cdb.common.exceptions.cdbException import CdbException

class CdbExceptionMapper:

    @classmethod
    def checkStatus(cls, httpHeaders):
        """ Map cdb status code into appropriate exception. """
        code = httpHeaders.get('Cdb-Status-Code', None)
        msg = httpHeaders.get('Cdb-Status-Message', 'Internal Error')
        if code is None or code == str(cdbStatus.CDB_OK):
            return
        elif cdbExceptionMap.CDB_EXCEPTION_MAP.has_key(int(code)):
            # Exception string is value of the form 'x.y.z'
            # where 'x.y' is cdb module, and 'z' class in that module
            exStr = cdbExceptionMap.CDB_EXCEPTION_MAP.get(int(code))
            exClass = exStr.split('.')[-1] # 'z' in 'x.y.z'
            exModule = '.'.join(exStr.split('.')[:-1]) # 'x.y' in 'x.y.z'
            exec 'from cdb.common.exceptions.%s import %s' % (exModule, exClass)
            exec 'ex = %s(msg)' % (exClass)
            raise ex
        else:
            raise CdbException(msg)

# Testing.
if __name__ == '__main__':
    pass
