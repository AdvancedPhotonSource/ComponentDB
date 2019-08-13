#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.objects.pdmLinkDrawing import PdmLinkDrawing

from cdb.cdb_web_service.api.pdmLinkRestApi import PdmLinkRestApi


class GetPdmLinkDrawingFromSearchResultCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--ufid', dest='ufid', help='PDMLink UFID from a search result.')
        self.addOption('', '--oid', dest='oid', help='PDMLink oid from a search result.')
        
    def checkArgs(self):
        ufid = self.options.ufid
        oid = self.options.oid

        if ufid is None:
            raise InvalidRequest('PDMLink UFID from a search result must be supplied.')
        if oid is None:
            raise InvalidRequest('PDMLink OID from a search result must be supplied.')
            
    def getUfid(self):
        return self.options.ufid

    def getOid(self):
        return self.options.oid

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-pdmlink-drawing-from-search-result --ufid=UFID --oid=OID

Description:
    Retrieves PDMLink drawing information from information that is included in a search result.
        """)
        self.checkArgs()
        api = PdmLinkRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        drawing = api.completeDrawingInformation(self.getUfid(),self.getOid())
        print drawing.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetPdmLinkDrawingFromSearchResultCli()
    cli.run()
