#!/usr/bin/env python

#
# Implementation for the PDMLink class
#

#######################################################################

from cdb.common.objects.cdbObjectManager import CdbObjectManager

from pdmLink import PdmLink

class PdmLinkControllerImpl(CdbObjectManager):
    def __init__(self):
        CdbObjectManager.__init__(self)
        self.pdmLink = PdmLink('edp', 'PakTai8', 'http://windchill-vm.aps.anl.gov/Windchill', 'https://icmsdocs.aps.anl.gov')

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

    def createComponent(self, drawingNumber, createdByUserId, componentTypeId, description, ownerUserId, ownerGroupId, isGroupWriteable, componentTypeName):
        return self.pdmLink.createComponent(drawingNumber, createdByUserId, componentTypeId, description, ownerUserId, ownerGroupId, isGroupWriteable, componentTypeName)
