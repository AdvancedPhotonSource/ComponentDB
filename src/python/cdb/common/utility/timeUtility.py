#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import time
class TimeUtility:

    @classmethod
    def getCurrentGMTimeStamp(cls):
        """ Formats GMT timestamp. """
        return time.strftime('%Y-%m-%d %H:%M:%S', time.gmtime(time.time()))

    @classmethod
    def formatGMTimeStamp(cls, t):
        """ Format GMT timestamp. """
        return time.strftime('%Y-%m-%d %H:%M:%S', time.gmtime(t))

    @classmethod
    def getCurrentLocalTimeStamp(cls):
        """ Formats local timestamp. """
        return time.strftime('%Y/%m/%d %H:%M:%S %Z', time.localtime(time.time()))

    @classmethod
    def formatLocalTimeStamp(cls, t):
        """ Formats local timestamp. """
        return time.strftime('%Y/%m/%d %H:%M:%S %Z', time.localtime(t))


