#!/usr/bin/env python

from cdb.common.cli.cdbRestCli import CdbRestCli

class CdbWebServiceCli(CdbRestCli):
    """ CDB web service cli class. """

    def __init__(self, validArgCount=0):
        CdbRestCli.__init__(self, validArgCount)

