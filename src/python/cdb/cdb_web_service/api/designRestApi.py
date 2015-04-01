#!/usr/bin/env python

import os
import urllib

from cdb.common.utility.encoder import Encoder
from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.api.cdbRestApi import CdbRestApi
from cdb.common.objects.design import Design

class DesignRestApi(CdbRestApi):
    
    def __init__(self, username=None, password=None, host=None, port=None, protocol=None):
        CdbRestApi.__init__(self, username, password, host, port, protocol)

    @CdbRestApi.execute
    def getDesigns(self):
        url = '%s/designs' % (self.getContextRoot())
        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, Design)

    @CdbRestApi.execute
    def getDesignById(self, id):
        url = '%s/designs/%s' % (self.getContextRoot(), id)
        if id is None:
            raise InvalidRequest('Design id must be provided.')
        responseData = self.sendRequest(url=url, method='GET')
        return Design(responseData)

    @CdbRestApi.execute
    def getDesignByName(self, name):
        url = '%s/designsByName/%s' % (self.getContextRoot(), name)
        if name is None or not len(name):
            raise InvalidRequest('Design name must be provided.')
        responseData = self.sendRequest(url=url, method='GET')
        return Design(responseData)

    @CdbRestApi.execute
    def addDesign(self, name, ownerUserId, ownerGroupId, isGroupWriteable, description):
        url = '%s/designs/add' % (self.getContextRoot())
        if name is None or not len(name):
            raise InvalidRequest('Design name must be provided.')
        url += '?name=%s' % Encoder.encode(name)
        if ownerUserId is not None:
            url += '&ownerUserId=%s' % ownerUserId
        if ownerGroupId is not None:
            url += '&ownerGroupId=%s' % ownerGroupId
        if description is not None and len(name):
            url += '&description=%s' % Encoder.encode(description)
        if isGroupWriteable is not None:
            url += '&isGroupWriteable=%s' % isGroupWriteable

        responseData = self.sendSessionRequest(url=url, method='POST', contentType='application/x-www-form-urlencoded')
        return Design(responseData)

    @CdbRestApi.execute
    def loadDesign(self, name, ownerUserId, ownerGroupId, isGroupWriteable, description, designElementList):
        url = '%s/designs/load' % (self.getContextRoot())
        if name is None or not len(name):
            raise InvalidRequest('Design name must be provided.')
        url += '?name=%s' % Encoder.encode(name)
        if ownerUserId is not None:
            url += '&ownerUserId=%s' % ownerUserId
        if ownerGroupId is not None:
            url += '&ownerGroupId=%s' % ownerGroupId
        if description is not None and len(name):
            url += '&description=%s' % Encoder.encode(description)
        if isGroupWriteable is not None:
            url += '&isGroupWriteable=%s' % isGroupWriteable
        if designElementList is not None and len(designElementList):
            url += '&designElementList=%s' % Encoder.encode(self.toJson(designElementList))

        responseData = self.sendSessionRequest(url=url, method='POST', contentType='application/x-www-form-urlencoded')
        return Design(responseData)

#######################################################################
# Testing.

if __name__ == '__main__':
    api = DesignRestApi('sveseli', 'sveseli', 'zagreb.svdev.net', 10232, 'http')
    print
    print 'Designs'
    print '**********'
    designs = api.getDesigns()
    for design in designs:
        print design.getDisplayString()


