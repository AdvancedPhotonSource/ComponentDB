#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.cli.cdbRestSessionCli import CdbRestSessionCli

class CdbWebServiceSessionCli(CdbRestSessionCli):
    """ CDB web service session cli class. """

    def __init__(self, validArgCount=0):
        CdbRestSessionCli.__init__(self, validArgCount)

