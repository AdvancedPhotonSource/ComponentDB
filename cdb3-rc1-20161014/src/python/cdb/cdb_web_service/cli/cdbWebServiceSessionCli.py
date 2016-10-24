#!/usr/bin/env python

from cdb.common.cli.cdbRestSessionCli import CdbRestSessionCli

class CdbWebServiceSessionCli(CdbRestSessionCli):
    """ CDB web service session cli class. """

    def __init__(self, validArgCount=0):
        CdbRestSessionCli.__init__(self, validArgCount)

