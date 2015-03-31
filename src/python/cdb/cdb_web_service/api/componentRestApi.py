#!/usr/bin/env python

import os
import urllib

from cdb.common.utility.encoder import Encoder
from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.api.cdbRestApi import CdbRestApi
from cdb.common.objects.component import Component
from cdb.common.objects.componentType import ComponentType
from cdb.common.objects.componentTypeCategory import ComponentTypeCategory

class ComponentRestApi(CdbRestApi):
    
    def __init__(self, username=None, password=None, host=None, port=None, protocol=None):
        CdbRestApi.__init__(self, username, password, host, port, protocol)

    def getComponentTypeCategories(self):
        try:
            url = '%s/componentTypeCategories' % (self.getContextRoot())
            responseData = self.sendRequest(url=url, method='GET')
            return self.toCdbObjectList(responseData, ComponentTypeCategory)
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)

    def getComponentTypes(self):
        try:
            url = '%s/componentTypes' % (self.getContextRoot())
            responseData = self.sendRequest(url=url, method='GET')
            return self.toCdbObjectList(responseData, ComponentType)
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)

    def getComponents(self):
        try:
            url = '%s/components' % (self.getContextRoot())
            responseData = self.sendRequest(url=url, method='GET')
            return self.toCdbObjectList(responseData, Component)
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)

    def getComponentById(self, id):
        try:
            url = '%s/components/%s' % (self.getContextRoot(), id)
            if id is None:
                raise InvalidRequest('Component id must be provided.')
            responseData = self.sendRequest(url=url, method='GET')
            return Component(responseData)
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)

    def getComponentByName(self, name):
        try:
            url = '%s/componentsByName/%s' % (self.getContextRoot(), name)
            if name is None or not len(name):
                raise InvalidRequest('Component name must be provided.')
            responseData = self.sendRequest(url=url, method='GET')
            return Component(responseData)
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)

    def addComponent(self, name, componentTypeId, ownerUserId, ownerGroupId, isGroupWriteable, description):
        try:
            url = '%s/components/add' % (self.getContextRoot())
            if name is None or not len(name):
                raise InvalidRequest('Component name must be provided.')
            url += '?name=%s' % Encoder.encode(name)
            if componentTypeId is None:
                raise InvalidRequest('Component type id must be provided.')
            url += '&componentTypeId=%s' % componentTypeId
            if ownerUserId is not None:
                url += '&ownerUserId=%s' % ownerUserId
            if ownerGroupId is not None:
                url += '&ownerGroupId=%s' % ownerGroupId
            if description is not None and len(name):
                url += '&description=%s' % Encoder.encode(description)
            if isGroupWriteable is not None:
                url += '&isGroupWriteable=%s' % isGroupWriteable

            responseData = self.sendSessionRequest(url=url, method='POST', contentType='application/x-www-form-urlencoded')
            return Component(responseData)
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)

#######################################################################
# Testing.

if __name__ == '__main__':
    api = ComponentRestApi('sveseli', 'sveseli', 'zagreb.svdev.net', 10232, 'http')
    componentTypes = api.getComponentTypes()
    print 'Component Types'
    print '***************'
    for componentType in componentTypes:
        print componentType.getDisplayString()

    print
    print 'Component Type Categories'
    print '*************************'
    componentTypeCategories = api.getComponentTypeCategories()
    for componentTypeCategory in componentTypeCategories:
        print componentTypeCategory.getDisplayString()

    print
    print 'Components'
    print '**********'
    components = api.getComponents()
    for component in components:
        print component.getDisplayString()




