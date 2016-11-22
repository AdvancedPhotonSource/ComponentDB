#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Implementation for the PDMLink class
#

#######################################################################

from cdb.common.objects.cdbObjectManager import CdbObjectManager
from pdmLink import PdmLink
import ConfigParser

class PdmLinkControllerImpl(CdbObjectManager):

    CONFIG_PATH = '../pdmLink.cfg'
    CONFIG_SECTION_NAME = 'PDMLink'
    CONFIG_PDMLINK_USER_KEY = 'pdmLinkUser'
    CONFIG_PDMLINK_PASS_KEY = 'pdmLinkPass'
    CONFIG_PDMLINK_URL_KEY = 'pdmLinkUrl'
    CONFIG_ICMS_URL_KEY = 'icmsUrl'
    CONFIG_ICMS_USER_KEY = 'icmsUser'
    CONFIG_ICMS_PASS_KEY = 'icmsPass'

    def __init__(self):
        CdbObjectManager.__init__(self)
        config = ConfigParser.ConfigParser()
        config.readfp(open(self.CONFIG_PATH))

        pdmlinkUser = config.get(self.CONFIG_SECTION_NAME, self.CONFIG_PDMLINK_USER_KEY)
        pdmlinkPass = config.get(self.CONFIG_SECTION_NAME, self.CONFIG_PDMLINK_PASS_KEY)
        pdmLinkUrl = config.get(self.CONFIG_SECTION_NAME, self.CONFIG_PDMLINK_URL_KEY)
        icmsUrl = config.get(self.CONFIG_SECTION_NAME, self.CONFIG_ICMS_URL_KEY)
        icmsUser = config.get(self.CONFIG_SECTION_NAME, self.CONFIG_ICMS_USER_KEY)
        icmsPass = config.get(self.CONFIG_SECTION_NAME, self.CONFIG_ICMS_PASS_KEY)

        self.pdmLink = PdmLink(pdmlinkUser, pdmlinkPass, pdmLinkUrl, icmsUrl, icmsUser, icmsPass)

    def getDrawing(self, drawingNumber):
        return self.pdmLink.getDrawing(drawingNumber)

    def getDrawings(self, searchPattern):
        return self.pdmLink.getDrawings(searchPattern)

    def getDrawingSearchResults(self, searchPattern):
        return self.pdmLink.getDrawingSearchResults(searchPattern)

    def getRelatedDrawingSearchResults(self, drawingNumberBase):
        return self.pdmLink.getRelatedDrawingSearchResults(drawingNumberBase)

    def completeDrawingInformation(self, ufid, oid):
        return self.pdmLink.completeDrawingInformation(ufid, oid)

    def getDrawingThumbnail(self, ufid):
        return self.pdmLink.getDrawingThumbnail(ufid)

    def getDrawingImage(self, ufid):
        return self.pdmLink.getDrawingImage(ufid)

    def generateComponentInfo(self, drawingNumber):
        return self.pdmLink.generateComponentInfo(drawingNumber)
