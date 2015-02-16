#!/usr/bin/env python

from cdb.common.utility.loggingManager import LoggingManager

class CdbDbEntityHandler:
    def __init__(self):
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)

    def getLogger(self):
        return self.logger

