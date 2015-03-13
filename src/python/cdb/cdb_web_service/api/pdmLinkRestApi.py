#!/usr/bin/env python

from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.api.cdbRestApi import CdbRestApi

from cdb.common.objects.pdmLinkDrawingRevisionsInfo import PDMLinkDrawingRevisionsInfo
from cdb.common.objects.pdmLinkRevisionThumbnail import PDMLinkRevisionThumbnail


class PDMLinkRestApi(CdbRestApi):
    
    def __init__(self, username=None, password=None, host=None, port=None, protocol=None):
        CdbRestApi.__init__(self, username, password, host, port, protocol)

    def getDrawingRevisionsInfo(self, drawingNumber):
        try:
            if drawingNumber is None:
                raise InvalidRequest('Drawing number must be provided.')
            url = '%s/pdmLink/drawingRevisionsInfo/%s' % (self.getContextRoot(), drawingNumber)
            responseData = self.sendRequest(url=url, method='GET')
            return PDMLinkDrawingRevisionsInfo(responseData)
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)

    def getRevisionThumbnail(self, revisionUfid):
        try:
            if revisionUfid is None:
                raise InvalidRequest('Revision ufid must be provided.')
            url = '%s/pdmLink/drawingRevisionThumbnail/%s' % (self.getContextRoot(), revisionUfid)
            responseData = self.sendRequest(url=url, method='GET')
            return PDMLinkRevisionThumbnail(responseData)
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)


#######################################################################
# Testing.

if __name__ == '__main__':
    api = PDMLinkRestApi('dariusz', 'dariusz', 'aodpc77.aps.anl.gov', 10232, 'http')
    revsLinks = api.getDrawingRevisionsInfo('U221020202-104210.DRW')
    print revsLinks
    print api.getRevisionThumbnail('OR:wt.epm.EPMDocument:3206233583:137301470-1321999281591-15287424-95-22-54-164@windchill-vm.aps.anl.gov')
    #for revLink in revsLinks:
    #    print revLink.getDisplayString()




