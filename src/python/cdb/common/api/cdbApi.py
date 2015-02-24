#!/usr/bin/env python

import json
from cdb.common.utility.loggingManager import LoggingManager

class CdbApi(object):
    """ Base cdb api class. """
    def __init__(self, username = None, password = None):
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)

    def getLogger(self):
        return self.logger

    @classmethod
    def toCdbObjectList(cls, dictList, cdbObjectClass):
        cdbObjectList = []
        for dict in dictList:
            cdbObjectList.append(cdbObjectClass(dict))
        return cdbObjectList 
      
        
