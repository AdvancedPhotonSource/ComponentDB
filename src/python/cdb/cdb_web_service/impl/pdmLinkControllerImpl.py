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

    def getDrawing(self, name):
        return self.pdmLink.getDrawing(name)

    def getDrawings(self, drawingNamePattern):
        return self.pdmLink.getDrawings(drawingNamePattern)

    def getDrawingSearchResults(self, drawingNamePattern):
        return self.pdmLink.getDrawingSearchResults(drawingNamePattern)

    def completeDrawingInformation(self, ufid, oid):
        return self.pdmLink.completeDrawingInformation(ufid, oid)

    def getDrawingThumbnail(self, ufid):
        return self.pdmLink.getDrawingThumbnail(ufid)

    def getDrawingImage(self, ufid):
        return self.pdmLink.getDrawingImage(ufid)
