#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Base object manager class.
#

#######################################################################

import threading
from cdb.common.utility.loggingManager import LoggingManager

#######################################################################

class CdbObjectManager:
    """ Base object manager class. """

    def __init__(self):
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)
        self.lock = threading.RLock()

    def getLogger(self):
        return self.logger

    def acquireLock(self):
        self.lock.acquire()

    def releaseLock(self):
        self.lock.release()

