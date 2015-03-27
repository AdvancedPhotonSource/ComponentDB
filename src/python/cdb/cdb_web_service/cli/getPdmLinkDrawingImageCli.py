#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.common.exceptions.invalidRequest import InvalidRequest

from cdb.cdb_web_service.api.pdmLinkRestApi import PdmLinkRestApi


class GetPdmLinkDrawingImageCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--ufid', dest='ufid', help='PDMLink drawing revision ufid.')
        
    def checkArgs(self):
        if self.options.ufid is None:
            raise InvalidRequest('PDMLink drawing revision revision ufid must be provided.')
            
    def getUfid(self):
        return self.options.ufid

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-pdmlink-drawing-thumbnail --ufid=UFID

Description:
    Retrieves image url for a given PDMLink drawing revision ufid.
        """)
        self.checkArgs()
        api = PdmLinkRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        image = api.getDrawingImage(self.getUfid())
        print image.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())


#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetPdmLinkDrawingImageCli()
    cli.run()
