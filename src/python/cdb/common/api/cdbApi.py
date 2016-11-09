#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import json
from cdb.common.exceptions.cdbException import CdbException
from cdb.common.utility.loggingManager import LoggingManager

class CdbApi(object):
    """ Base cdb api class. """
    def __init__(self, username = None, password = None):
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)

    @classmethod
    def getLogger(cls):
        logger = LoggingManager.getInstance().getLogger(cls.__name__)
        return logger

    @classmethod
    def toCdbObjectList(cls, dictList, cdbObjectClass):
        cdbObjectList = []
        for dict in dictList:
            cdbObjectList.append(cdbObjectClass(dict))
        return cdbObjectList 

    # Exception decorator for all api calls
    @classmethod
    def execute(cls, func):
        def decorate(*args, **kwargs):
            try:
                response = func(*args, **kwargs)
                return response
            except CdbException, ex:
                raise
            except Exception, ex:
                cls.getLogger().exception('%s' % ex)
                raise CdbException(exception=ex)
        return decorate
