#!/usr/bin/env python

from cdb.common.utility.loggingManager import LoggingManager

class CdbApi(object):
    """ Base cdb api class. """
    def __init__(self, username = None, password = None):
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)

    def getLogger(self):
        return self.logger
