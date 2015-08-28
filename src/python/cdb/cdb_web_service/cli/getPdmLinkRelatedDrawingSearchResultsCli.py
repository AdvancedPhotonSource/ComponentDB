#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.cdb_web_service.api.pdmLinkRestApi import PdmLinkRestApi


class GetPdmLinkDrawingCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--drawing-number-base', dest='drawingNumberBase', help='.')
        
    def checkArgs(self):
        drawingNumberBase = self.getDrawingNumberBase()
        if drawingNumberBase is None:
            raise InvalidRequest('A full PDMLink drawing number or drawing number without extension must be provided.')
            
    def getDrawingNumberBase(self):
        return self.options.drawingNumberBase

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-pdmlink-related-search-results --drawing-number-base=DRAWINGNUMBERBASE

Description:
    Retrieves PDMLink drawings that are related to each other.
        """)
        self.checkArgs()
        api = PdmLinkRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        results = api.getRelatedDrawingSearchResults(self.getDrawingNumberBase())
        for result in results:
            print result.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetPdmLinkDrawingCli()
    cli.run()
