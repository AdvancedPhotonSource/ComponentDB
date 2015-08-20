#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.objects.pdmLinkDrawing import PdmLinkDrawing

from cdb.cdb_web_service.api.pdmLinkRestApi import PdmLinkRestApi


class GetPdmLinkDrawingCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--drawing-number', dest='drawingNumber', help='PDMLink drawing number (valid extensions: %s).' % PdmLinkDrawing.VALID_EXTENSION_LIST)
        
    def checkArgs(self):
        drawingNumber = self.options.drawingNumber
        if drawingNumber is None:
            raise InvalidRequest('PDMLink drawing number must be supplied.')
        PdmLinkDrawing.checkDrawingNumber(drawingNumber)
            
    def getDrawingNumber(self):
        return self.options.drawingNumber

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-pdmlink-drawing --drawing-number=DRAWINGNUMBER

Description:
    Retrieves PDMLink drawing information.
        """)
        self.checkArgs()
        api = PdmLinkRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        drawing = api.getDrawing(self.getDrawingNumber())
        print drawing.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetPdmLinkDrawingCli()
    cli.run()
