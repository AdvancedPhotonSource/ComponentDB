#!/usr/bin/env python

import os
import urllib

from cdb.common.utility.encoder import Encoder
from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.api.cdbRestApi import CdbRestApi
from cdb.common.objects.design import Design
from cdb.common.objects.designProperty import DesignProperty
from cdb.common.objects.designElementProperty import DesignElementProperty

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

    @CdbRestApi.execute
    def addDesignProperty(self, designId, propertyTypeId, tag, value, units, description, isDynamic, isUserWriteable):
        if designId is None:
            raise InvalidRequest('Design id must be provided.')
        if propertyTypeId is None:
            raise InvalidRequest('Property type id must be provided.')

        url = '%s/designs/%s/propertiesByType/%s' % (self.getContextRoot(), designId, propertyTypeId)

        parameters = ''
        if tag:
            parameters += '&tag=%s' % Encoder.encode(tag)
        if value:
            parameters += '&value=%s' % Encoder.encode(value)
        if units:
            parameters += '&units=%s' % Encoder.encode(units)
        if description:
            parameters += '&description=%s' % Encoder.encode(description)
        if isDynamic is not None:
            parameters += '&isDynamic=%s' % isDynamic
        if isUserWriteable is not None:
            parameters += '&isUserWriteable=%s' % isUserWriteable
        if len(parameters):
            url += '?%s' % parameters[1:]

        responseData = self.sendSessionRequest(url=url, method='POST', contentType='application/x-www-form-urlencoded')
        return DesignProperty(responseData)

    @CdbRestApi.execute
    def addDesignElementProperty(self, designElementId, propertyTypeId, tag, value, units, description, isDynamic, isUserWriteable):
        if designElementId is None:
            raise InvalidRequest('Design element id must be provided.')
        if propertyTypeId is None:
            raise InvalidRequest('Property type id must be provided.')

        url = '%s/designElements/%s/propertiesByType/%s' % (self.getContextRoot(), designElementId, propertyTypeId)

        parameters = ''
        if tag:
            parameters += '&tag=%s' % Encoder.encode(tag)
        if value:
            parameters += '&value=%s' % Encoder.encode(value)
        if units:
            parameters += '&units=%s' % Encoder.encode(units)
        if description:
            parameters += '&description=%s' % Encoder.encode(description)
        if isDynamic is not None:
            parameters += '&isDynamic=%s' % isDynamic
        if isUserWriteable is not None:
            parameters += '&isUserWriteable=%s' % isUserWriteable
        if len(parameters):
            url += '?%s' % parameters[1:]

        responseData = self.sendSessionRequest(url=url, method='POST', contentType='application/x-www-form-urlencoded')
        return DesignElementProperty(responseData)

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


