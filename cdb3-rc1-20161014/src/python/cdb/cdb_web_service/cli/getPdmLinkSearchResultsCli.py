#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.common.exceptions.invalidRequest import InvalidRequest

from cdb.cdb_web_service.api.pdmLinkRestApi import PdmLinkRestApi


class GetPdmLinkDrawingCli(CdbWebServiceCli):
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
    cdb-get-pdmlink-search-results --search-pattern=SEARCHPATTERN

Description:
    Retrieves PDMLink search results.
        """)
        self.checkArgs()
        api = PdmLinkRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        results = api.getDrawingSearchResults(self.getSearchPattern())
        for result in results:
            print result.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetPdmLinkDrawingCli()
    cli.run()
