#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Implementation for the PDMLink class
#

#######################################################################

from icms import Icms
from cdb.common.objects.cdbObjectManager import CdbObjectManager
import ConfigParser
import os

class IcmsControllerImpl(CdbObjectManager):

    PYTHON_FILE_PATH = os.path.abspath(os.path.dirname(__file__))
    CONFIG_FILE_PATH = os.path.join(PYTHON_FILE_PATH, '..', 'pdmLink.cfg')
    CONFIG_SECTION_NAME = 'PDMLink'
    CONFIG_ICMS_URL_KEY = 'icmsUrl'
    CONFIG_ICMS_USER_KEY = 'icmsUser'
    CONFIG_ICMS_PASS_KEY = 'icmsPass'

    def __init__(self):
        CdbObjectManager.__init__(self)
        config = ConfigParser.ConfigParser()
        config.readfp(open(self.CONFIG_FILE_PATH))

        icmsUrl = config.get(self.CONFIG_SECTION_NAME, self.CONFIG_ICMS_URL_KEY)
        icmsUser = config.get(self.CONFIG_SECTION_NAME, self.CONFIG_ICMS_USER_KEY)
        icmsPass = config.get(self.CONFIG_SECTION_NAME, self.CONFIG_ICMS_PASS_KEY)

        self.icms = Icms(icmsUser, icmsPass, icmsUrl)

    def performQuickSearch(self, searchPattern):
        return self.icms.performIcmsQuickSearch(searchPattern)

    def getDocInfo(self, docName):
        return self.icms.getDocInfo(docName)

