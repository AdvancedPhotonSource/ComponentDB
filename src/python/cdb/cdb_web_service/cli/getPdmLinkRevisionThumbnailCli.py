#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.common.exceptions.invalidRequest import InvalidRequest

from cdb.cdb_web_service.api.pdmLinkRestApi import PDMLinkRestApi


class GetPDMLinkRevisionThumbnailCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--revision-ufid', dest='revisionufid', help='A PDMLink revision ufid must be provided.')
        
    def checkArgs(self):
        if self.options.revisionufid is None:
            raise InvalidRequest('A PDMLink revision ufid must be provided.')
            
    def getRevisionUfid(self):
        return self.options.revisionufid

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-pdmlink-revision-thumbnail --revision-ufid=REVISIONUFID

Description:
    Retrieves a thumbnail image url for a given ufid of a specific revision of the drawing.
        """)
        self.checkArgs()
        api = PDMLinkRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        revisionThubnail = api.getRevisionThumbnail(self.getRevisionUfid())
        print revisionThubnail.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())



#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetPDMLinkRevisionThumbnailCli()
    cli.run()
