#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.utility.errorChecker import ErrorChecker
from cdb.common.objects.pdmLinkDrawingRevisionsInfo import PDMLinkDrawingRevisionsInfo

from cdb.cdb_web_service.api.pdmLinkRestApi import PDMLinkRestApi


class GetPDMLinkDrawingRevisionsInfoCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--drawing-number', dest='drawingNumber', help='A PDMLink drawing number must be supplied.')
        
    def checkArgs(self):
        if self.options.drawingNumber is None:
            raise InvalidRequest('A PDMLink drawing number must be supplied.')
        ErrorChecker.pdmLinkDrawingValidExtension(self.getDrawingNumber())
            
    def getDrawingNumber(self):
        return self.options.drawingNumber

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-pdmlink-revision-info --drawing-number=DRAWINGNUMBER

Description:
    Retrieves revision(Rev, ICMS link, Winchill link, and status) information given a drawing number 
        """)
        self.checkArgs()
        api = PDMLinkRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        drawingRevisionsInfo = api.getDrawingRevisionsInfo(self.getDrawingNumber())
        dispString = drawingRevisionsInfo.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())
        print str(dispString).split('[')[0]
        print '['
        for revisionDict in drawingRevisionsInfo['revisions']:
            revision = PDMLinkDrawingRevisionsInfo(revisionDict)
            print revision.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())
        print ']'


#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetPDMLinkDrawingRevisionsInfoCli()
    cli.run()
