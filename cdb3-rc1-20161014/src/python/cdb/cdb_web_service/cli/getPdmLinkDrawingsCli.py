#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.common.exceptions.invalidRequest import InvalidRequest

from cdb.cdb_web_service.api.pdmLinkRestApi import PdmLinkRestApi


class GetPdmLinkDrawingsCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--search-pattern', dest='searchPattern', help='PDMLink drawing search pattern using keywords/wildcards(*/?) must be provided.')
        
    def checkArgs(self):
        searchPattern = self.getSearchPattern()
        if searchPattern is None:
            raise InvalidRequest('PDMLink drawing search pattern using keywords/wildcards(*/?) must be provided.')
            
    def getSearchPattern(self):
        return self.options.searchPattern

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-pdmlink-drawings --search-pattern=SEARCHPATTERN

Description:
    Retrieves PDMLink drawing information for multiple drawings.
        """)
        self.checkArgs()
        api = PdmLinkRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        drawings = api.getDrawings(self.getSearchPattern())
        for drawing in drawings:
            print drawing.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetPdmLinkDrawingsCli()
    cli.run()
