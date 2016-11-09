#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.cli.cdbRestCli import CdbRestCli

class CdbWebServiceCli(CdbRestCli):
    """ CDB web service cli class. """

    def __init__(self, validArgCount=0):
        CdbRestCli.__init__(self, validArgCount)

