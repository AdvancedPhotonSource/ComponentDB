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

    def getComponentTypeCategoryList(self):
        try:
            url = '%s/componentTypeCategories' % (self.getContextRoot())
            responseData = self.sendRequest(url=url, method='GET')
            return self.toCdbObjectList(responseData, ComponentTypeCategory)
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)

    def getComponentTypeList(self):
        try:
            url = '%s/componentTypes' % (self.getContextRoot())
            responseData = self.sendRequest(url=url, method='GET')
            return self.toCdbObjectList(responseData, ComponentType)
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)

    def getComponentList(self):
        try:
            url = '%s/components' % (self.getContextRoot())
            responseData = self.sendRequest(url=url, method='GET')
            return self.toCdbObjectList(responseData, Component)
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)

#######################################################################
# Testing.

if __name__ == '__main__':
    api = ComponentRestApi('sveseli', 'sveseli', 'zagreb.svdev.net', 10232, 'http')
    componentTypeList = api.getComponentTypeList()
    print 'Component Types'
    print '***************'
    for componentType in componentTypeList:
        print componentType.getDisplayString()

    print
    print 'Component Type Categories'
    print '*************************'
    componentTypeCategoryList = api.getComponentTypeCategoryList()
    for componentTypeCategory in componentTypeCategoryList:
        print componentTypeCategory.getDisplayString()

    print
    print 'Components'
    print '**********'
    componentList = api.getComponentList()
    for component in componentList:
        print component.getDisplayString()




