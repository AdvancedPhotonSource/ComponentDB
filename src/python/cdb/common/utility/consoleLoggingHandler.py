#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Console logging handler class
#

#######################################################################

import socket
import pwd
import os
from logging import StreamHandler

#######################################################################

class ConsoleLoggingHandler(StreamHandler):
    """ Class that enables console logging. """
    def __init__(self, *args):
        StreamHandler.__init__(self, *args)
        self.user = pwd.getpwuid(os.getuid())[0]
        self.host = socket.gethostname()

    def emit(self, record):
        record.__dict__['user'] = self.user
        record.__dict__['host'] = self.host
        return StreamHandler.emit(self, record)

#######################################################################
# Testing.

if __name__ == '__main__':
    import sys
    import logging
    exec 'sh = ConsoleLoggingHandler(sys.stdout,)'
    sh.setLevel(logging.INFO)
    rootLogger = logging.getLogger('')
    logging.basicConfig(level=logging.DEBUG)

    mainLogger = logging.getLogger('main')
    mainLogger.debug("main debug")
    mainLogger.info("main info")
                                                                                                                        
