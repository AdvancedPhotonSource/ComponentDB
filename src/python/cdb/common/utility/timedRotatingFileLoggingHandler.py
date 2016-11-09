#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#######################################################################

import socket
import pwd
import os
from logging.handlers import TimedRotatingFileHandler

#######################################################################

class TimedRotatingFileLoggingHandler(TimedRotatingFileHandler):
    """ Class that enables logging into files. """
    def __init__(self, filename, when='D', interval=1, backupCount=0, encoding=None):
        TimedRotatingFileHandler.__init__(self, filename, when, interval, backupCount, encoding)
        self.user = pwd.getpwuid(os.getuid())[0]
        self.host = socket.gethostname()

    def emit(self, record):
        record.__dict__['user'] = self.user
        record.__dict__['host'] = self.host
        return TimedRotatingFileHandler.emit(self, record)
                                                                                
#######################################################################
# Testing.

if __name__ == '__main__':
    pass
