#!/usr/bin/env python

from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.api.cdbRestApi import CdbRestApi

from cdb.common.objects.pdmLinkDrawing import PdmLinkDrawing
from cdb.common.objects.pdmLinkComponent import PdmLinkComponent
from cdb.common.objects.pdmLinkSearchResult import PdmLinkSearchResult
from cdb.common.objects.image import Image
from cdb.common.utility.encoder import Encoder


class PdmLinkRestApi(CdbRestApi):
   
    def __init__(self, username=None, password=None, host=None, port=None, protocol=None):
        CdbRestApi.__init__(self, username, password, host, port, protocol)

    def __applyURLCharCodes(self, searchPattern):
        """
        HTTP GET methods has special codes for some characters

        :param searchPattern: A variable that is used to perform search. May include wildcard chars such as ? or *
        :return: a search pattern with the URL Character code applied
        """
        searchPattern = str(searchPattern).replace("?", "%3F")
        searchPattern = str(searchPattern).replace(" ", "%20")
        return str(searchPattern).replace("?", "%3F")

    @CdbRestApi.execute
    def getDrawing(self, drawingNumber):
        if drawingNumber is None:
            raise InvalidRequest('Drawing number must be provided.')
        url = '%s/pdmLink/drawings/%s' % (self.getContextRoot(), drawingNumber)
        responseData = self.sendRequest(url=url, method='GET')
        return PdmLinkDrawing(responseData)

    @CdbRestApi.execute
    def getDrawings(self, searchPattern):
        if searchPattern is None:
            raise InvalidRequest('PDMLink drawing search pattern using keywords/wildcards(*/?) must be provided.')
        searchPattern = self.__applyURLCharCodes(searchPattern)
        url = '%s/pdmLink/drawingsByKeyword/%s' % (self.getContextRoot(), searchPattern)
        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, PdmLinkDrawing)

    @CdbRestApi.execute
    def getDrawingSearchResults(self, searchPattern):
        if searchPattern is None:
            raise InvalidRequest('PDMLink drawing search pattern using keywords/wildcards(*/?) must be provided.')
        searchPattern = self.__applyURLCharCodes(searchPattern)
        url = '%s/pdmLink/search/%s' % (self.getContextRoot(), searchPattern)
        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, PdmLinkSearchResult)

    @CdbRestApi.execute
    def getRelatedDrawingSearchResults(self, drawingNumberBase):
        if drawingNumberBase is None:
            raise InvalidRequest("Drawing number base with the full drawing number or drawing number before extension must be provided")
        url = '%s/pdmLink/searchRelated/%s' % (self.getContextRoot(), drawingNumberBase)
        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, PdmLinkSearchResult)

    @CdbRestApi.execute
    def completeDrawingInformation(self, ufid, oid):
        if ufid is None:
            raise InvalidRequest('A drawing ufid must be provided.')
        if oid is None:
            raise InvalidRequest('A drawing oid must be provided.')
        url = '%s/pdmLink/completeDrawings/%s/%s' % (self.getContextRoot(), ufid, oid)
        responseData = self.sendRequest(url=url, method='GET')
        return PdmLinkDrawing(responseData)

    @CdbRestApi.execute
    def getDrawingThumbnail(self, ufid):
        if ufid is None:
            raise InvalidRequest('Drawing revision ufid must be provided.')
        url = '%s/pdmLink/drawingThumbnails/%s' % (self.getContextRoot(), ufid)
        responseData = self.sendRequest(url=url, method='GET')
        return Image(responseData)

    @CdbRestApi.execute
    def getDrawingImage(self, ufid):
        if ufid is None:
            raise InvalidRequest('Drawing revision ufid must be provided.')
        url = '%s/pdmLink/drawingImages/%s' % (self.getContextRoot(), ufid)
        responseData = self.sendRequest(url=url, method='GET')
        return Image(responseData)

    @CdbRestApi.execute
    def generateComponentInfo(self, drawingNumber):
        if drawingNumber is None:
            raise InvalidRequest('Drawing number must be provided.')
        url = '%s/pdmLink/componentInfo/%s' % (self.getContextRoot(), drawingNumber)
        responseData = self.sendRequest(url=url, method='GET')
        return PdmLinkComponent(responseData)


#######################################################################
# Testing.

if __name__ == '__main__':
    api = PdmLinkRestApi('sveseli', 'sveseli', 'localhost', 10232, 'http')
    drawing = api.getDrawing('U221020202-104210.DRW')
    print drawing
    print api.getDrawingThumbnail('OR:wt.epm.EPMDocument:3206233583:137301470-1321999281591-15287424-95-22-54-164@windchill-vm.aps.anl.gov')




