#!/usr/bin/env python

#
# Component route descriptor.
#

from cdb.common.utility.configurationManager import ConfigurationManager
from componentTypeCategoryController import ComponentTypeCategoryController
from componentTypeController import ComponentTypeController
from componentController import ComponentController
from componentSessionController import ComponentSessionController

class ComponentRouteDescriptor:

    @classmethod
    def getRoutes(cls):
        contextRoot = ConfigurationManager.getInstance().getContextRoot()

        # Static instances shared between different routes
        componentTypeCategoryController = ComponentTypeCategoryController()
        componentTypeController = ComponentTypeController()
        componentController = ComponentController()
        componentSessionController = ComponentSessionController()

        # Define routes.
        # Make sure to have leading '/' for consistency.
        routes = [

            # Get component type category list
            {
                'name' : 'getComponentTypeCategories',
                'path' : '%s/componentTypeCategories' % contextRoot,
                'controller' : componentTypeCategoryController,
                'action' : 'getComponentTypeCategories', 
                'method' : ['GET']
            },

            # Get component type list
            {
                'name' : 'getComponentTypes',
                'path' : '%s/componentTypes' % contextRoot,
                'controller' : componentTypeController,
                'action' : 'getComponentTypes', 
                'method' : ['GET']
            },

            # Get component list
            {
                'name' : 'getComponents',
                'path' : '%s/components' % contextRoot,
                'controller' : componentController,
                'action' : 'getComponents', 
                'method' : ['GET']
            },

            # Get component by id
            {
                'name' : 'getComponentById',
                'path' : '%s/components/:(id)' % contextRoot,
                'controller' : componentController,
                'action' : 'getComponentById', 
                'method' : ['GET']
            },

            # Get component by name
            {
                'name' : 'getComponentByName',
                'path' : '%s/componentsByName/:(name)' % contextRoot,
                'controller' : componentController,
                'action' : 'getComponentByName', 
                'method' : ['GET']
            },

            # Add component 
            # We cannot use route like componentsByName/:(name) since
            # component names can contain slashes
            {
                'name' : 'addComponent',
                'path' : '%s/components/add' % contextRoot,
                'controller' : componentSessionController,
                'action' : 'addComponent', 
                'method' : ['POST']
            },

            # Add component property
            {
                'name' : 'addComponentProperty',
                'path' : '%s/components/:(componentId)/propertiesByType/:(propertyTypeId)' % contextRoot,
                'controller' : componentSessionController,
                'action' : 'addComponentProperty', 
                'method' : ['POST']
            },

            # Add component instance property
            {
                'name' : 'addComponentInstanceProperty',
                'path' : '%s/componentInstances/:(componentInstanceId)/propertiesByType/:(propertyTypeId)' % contextRoot,
                'controller' : componentSessionController,
                'action' : 'addComponentInstanceProperty', 
                'method' : ['POST']
            },
        ]
       
        return routes


