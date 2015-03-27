#!/usr/bin/env python

from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.api.cdbRestApi import CdbRestApi

from cdb.common.objects.pdmLinkDrawing import PdmLinkDrawing
from cdb.common.objects.image import Image


class PdmLinkRestApi(CdbRestApi):
   
    def __init__(self, username=None, password=None, host=None, port=None, protocol=None):
        CdbRestApi.__init__(self, username, password, host, port, protocol)

    def getDrawing(self, name):
        try:
            if name is None:
                raise InvalidRequest('Drawing name must be provided.')
            url = '%s/pdmLink/drawings/%s' % (self.getContextRoot(), name)
            responseData = self.sendRequest(url=url, method='GET')
            return PdmLinkDrawing(responseData)
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)

    def getDrawingThumbnail(self, ufid):
        try:
            if ufid is None:
                raise InvalidRequest('Drawing revision ufid must be provided.')
            url = '%s/pdmLink/drawingThumbnails/%s' % (self.getContextRoot(), ufid)
            responseData = self.sendRequest(url=url, method='GET')
            return Image(responseData)
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)

    def getDrawingImage(self, ufid):
        try:
            if ufid is None:
                raise InvalidRequest('Drawing revision ufid must be provided.')
            url = '%s/pdmLink/drawingImages/%s' % (self.getContextRoot(), ufid)
            responseData = self.sendRequest(url=url, method='GET')
            return Image(responseData)
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)


#######################################################################
# Testing.

if __name__ == '__main__':
    api = PdmLinkRestApi('sveseli', 'sveseli', 'localhost', 10232, 'http')
    drawing = api.getDrawing('U221020202-104210.DRW')
    print drawing
    print api.getDrawingThumbnail('OR:wt.epm.EPMDocument:3206233583:137301470-1321999281591-15287424-95-22-54-164@windchill-vm.aps.anl.gov')




