#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Module manager class.
#

#######################################################################

import threading

#######################################################################

class CdbModuleManager:
    """ Singleton class used for managing cdb modules. """

    # Get singleton instance.
    @classmethod
    def getInstance(cls):
        from cdb.common.utility.cdbModuleManager import CdbModuleManager
        try:
            mgr = CdbModuleManager()
        except CdbModuleManager, ex:
            mgr = ex
        return mgr

    # Singleton.
    __instanceLock = threading.RLock()
    __instance = None

    def __init__(self):
        CdbModuleManager.__instanceLock.acquire()
        try:
            if CdbModuleManager.__instance:
                raise CdbModuleManager.__instance
            CdbModuleManager.__instance = self
            from cdb.common.utility.loggingManager import LoggingManager
            self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)
            self.lock = threading.RLock()
            self.moduleList = []
            self.modulesRunning = False
        finally:
            CdbModuleManager.__instanceLock.release()

    def addModule(self, m):
        self.lock.acquire()
        try:
            self.logger.debug('Adding cdb module: %s' % m.__class__.__name__)
            self.moduleList.append(m)
        finally:
            self.lock.release()

    def startModules(self):
        self.lock.acquire()
        try:
            if self.modulesRunning:
                return
            for m in self.moduleList:
                self.logger.debug('Starting cdb module: %s' % m.__class__.__name__)
                m.start()
            self.modulesRunning = True
        finally:
            self.lock.release()

    def stopModules(self):
        self.lock.acquire()
        try:
            if not self.modulesRunning:
                return
            n = len(self.moduleList)
            for i in range(0, n):
                m = self.moduleList[n-1-i]
                self.logger.debug('Stopping cdb module: %s' % m.__class__.__name__)
                m.stop()
            self.modulesRunning = False
        finally:
            self.lock.release()

#######################################################################
# Testing.

if __name__ == '__main__':
    pass
