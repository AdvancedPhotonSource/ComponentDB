#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.objects.pdmLinkDrawing import PdmLinkDrawing

from cdb.cdb_web_service.api.pdmLinkRestApi import PdmLinkRestApi


class GetPdmLinkDrawingCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--name', dest='name', help='PDMLink drawing name (valid extensions: %s).' % PdmLinkDrawing.VALID_EXTENSION_LIST)
        
    def checkArgs(self):
        name = self.options.name
        if name is None:
            raise InvalidRequest('PDMLink drawing name must be supplied.')
        PdmLinkDrawing.checkDrawingName(name)
            
    def getName(self):
        return self.options.name

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-pdmlink-drawing --name=NAME

Description:
    Retrieves PDMLink drawing information.
        """)
        self.checkArgs()
        api = PdmLinkRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        drawing = api.getDrawing(self.getName())
        print drawing.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetPdmLinkDrawingCli()
    cli.run()
