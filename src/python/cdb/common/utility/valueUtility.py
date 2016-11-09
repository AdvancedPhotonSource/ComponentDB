#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


class ValueUtility:

    @classmethod
    def toBoolean(cls, value):
        if value is None:
            return False
        if str(value).lower() == 'true':
            return True
        return False

#######################################################################
# Testing.
if __name__ == '__main__':
    print ValueUtility.toBoolean('false')
    print ValueUtility.toBoolean('True')
    print ValueUtility.toBoolean(True)

