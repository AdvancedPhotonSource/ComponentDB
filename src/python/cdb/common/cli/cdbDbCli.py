#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.cli.cdbCli import CdbCli

class CdbDbCli(CdbCli):
    """ Base DB cli class. """

    def __init__(self, validArgCount=0):
        CdbCli.__init__(self, validArgCount)


