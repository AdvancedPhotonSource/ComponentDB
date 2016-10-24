#!/usr/bin/env python

from cdb.common.cli.cdbCli import CdbCli

class CdbDbCli(CdbCli):
    """ Base DB cli class. """

    def __init__(self, validArgCount=0):
        CdbCli.__init__(self, validArgCount)


