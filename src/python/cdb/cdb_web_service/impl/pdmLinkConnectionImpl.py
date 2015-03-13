#!/usr/bin/env python

#
# Implementation for the PDMLink class
#

#######################################################################

from cdb.common.objects.cdbObjectManager import CdbObjectManager

from pdmLinkConnection import PDMLink


class PDMLinkConnectionImpl(CdbObjectManager):
    def __init__(self):
        CdbObjectManager.__init__(self)
        self.pdmLinkConnection = PDMLink('edp', 'PakTai8', 'http://windchill-vm.aps.anl.gov/Windchill',
                                         'https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=',
                                         '&dRevLabel=')

    def getDrawingRevisionsInfo(self, drawingNumber):
        return self.pdmLinkConnection.getLinksRevs(drawingNumber)

    def getDrawingThumbnail(self, drawingRevUfid):
        return self.pdmLinkConnection.getThumbnail(drawingRevUfid)