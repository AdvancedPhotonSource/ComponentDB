#!/usr/bin/env python

import os
import urllib

from cdb.common.utility.encoder import Encoder
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.api.cdbRestApi import CdbRestApi
from cdb.common.objects.component import Component
from cdb.common.objects.componentInstance import ComponentInstance
from cdb.common.objects.componentProperty import ComponentProperty
from cdb.common.objects.componentInstanceProperty import ComponentInstanceProperty
from cdb.common.objects.componentType import ComponentType
from cdb.common.objects.componentTypeCategory import ComponentTypeCategory

class ComponentRestApi(CdbRestApi):
    
    def __init__(self, username=None, password=None, host=None, port=None, protocol=None):
        CdbRestApi.__init__(self, username, password, host, port, protocol)

    @CdbRestApi.execute
    def getComponentTypeCategories(self):
        url = '%s/componentTypeCategories' % (self.getContextRoot())
        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, ComponentTypeCategory)

    @CdbRestApi.execute
    def getComponentTypes(self):
        url = '%s/componentTypes' % (self.getContextRoot())
        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, ComponentType)

    @CdbRestApi.execute
    def getComponents(self):
        url = '%s/components' % (self.getContextRoot())
        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, Component)

    @CdbRestApi.execute
    def getComponentsByName(self, name):
        url = '%s/componentsByName/%s' % (self.getContextRoot(), Encoder.encode(name))
        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, Component)

    @CdbRestApi.execute
    def getComponentById(self, id):
        if id is None:
            raise InvalidRequest('Component id must be provided.')
        url = '%s/componentById/%s' % (self.getContextRoot(), id)
        responseData = self.sendRequest(url=url, method='GET')
        return Component(responseData)

    @CdbRestApi.execute
    def getComponentByName(self, name):
        if name is None or not len(name):
            raise InvalidRequest('Component name must be provided.')
        url = '%s/componentByName/%s' % (self.getContextRoot(), Encoder.encode(name))
        responseData = self.sendRequest(url=url, method='GET')
        return Component(responseData)

    @CdbRestApi.execute
    def getComponentByModelNumber(self, modelNumber):
        if not modelNumber:
            raise InvalidRequest('Component model number must be provided.')
        url = '%s/componentByModelNumber/%s' % (self.getContextRoot(), Encoder.encode(modelNumber))
        responseData = self.sendRequest(url=url, method='GET')
        return Component(responseData)

    @CdbRestApi.execute
    def getComponentInstanceById(self, id):
        if id is None:
            raise InvalidRequest('Component Instance id must be provided.')
        url = '%s/componentInstanceById/%s' % (self.getContextRoot(), id)
        responseData = self.sendRequest(url=url, method='GET')
        return ComponentInstance(responseData)

    @CdbRestApi.execute
    def addComponent(self, name, modelNumber, componentTypeId, ownerUserId, ownerGroupId, isGroupWriteable, description):
        url = '%s/addComponent' % (self.getContextRoot())
        if name is None or not len(name):
            raise InvalidRequest('Component name must be provided.')
        url += '?name=%s' % Encoder.encode(name)
        if modelNumber:
            url += '&modelNumber=%s' % Encoder.encode(modelNumber)
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

    @CdbRestApi.execute
    def addComponentProperty(self, componentId, propertyTypeId, tag, value, units, description, isDynamic, isUserWriteable):
        if componentId is None:
            raise InvalidRequest('Component id must be provided.')
        if propertyTypeId is None:
            raise InvalidRequest('Property type id must be provided.')

        url = '%s/components/%s/propertiesByType/%s' % (self.getContextRoot(), componentId, propertyTypeId)

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
        return ComponentProperty(responseData)

    @CdbRestApi.execute
    def addComponentInstanceProperty(self, componentInstanceId, propertyTypeId, tag, value, units, description, isDynamic, isUserWriteable):
        if componentInstanceId is None:
            raise InvalidRequest('Component instance id must be provided.')
        if propertyTypeId is None:
            raise InvalidRequest('Property type id must be provided.')

        url = '%s/componentInstances/%s/propertiesByType/%s' % (self.getContextRoot(), componentInstanceId, propertyTypeId)

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
        return ComponentInstanceProperty(responseData)

#######################################################################
# Testing.

if __name__ == '__main__':
    api = ComponentRestApi('sveseli', 'sv', 'zagreb.svdev.net', 10232, 'http')
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

    print
    print 'Components By Name'
    print '******************'
    components = api.getComponentsByName('3458A DVM')
    for component in components:
        print component.getDisplayString()

    #component = api.getComponentByName('3458A DVM')
    component = api.getComponentByModelNumber('xyz0001')
    print component

    print
    #print 'Adding Component'
    #component = api.addComponent('3458A DVM', 'xyz0001', 11, 4, 3, False, 'my new component')
    #print component

    #print
    #print 'Adding Component Property'
    #print '*************************'
    #print api.addComponentProperty(componentId=10, propertyTypeId=2, tag='mytag2', value='A', units=None, description=None, isDynamic=False, isUserWriteable=False)



