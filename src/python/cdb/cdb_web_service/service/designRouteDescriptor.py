#!/usr/bin/env python

#
# Design route descriptor.
#

from cdb.common.utility.configurationManager import ConfigurationManager
from designController import DesignController
from designSessionController import DesignSessionController

class DesignRouteDescriptor:

    @classmethod
    def getRoutes(cls):
        contextRoot = ConfigurationManager.getInstance().getContextRoot()

        # Static instances shared between different routes
        designController = DesignController()
        designSessionController = DesignSessionController()

        # Define routes.
        # Make sure to have leading '/' for consistency.
        routes = [

            # Get design list
            {
                'name' : 'getDesigns',
                'path' : '%s/designs' % contextRoot,
                'controller' : designController,
                'action' : 'getDesigns', 
                'method' : ['GET']
            },

            # Get design by id
            {
                'name' : 'getDesignById',
                'path' : '%s/designs/:(id)' % contextRoot,
                'controller' : designController,
                'action' : 'getDesignById', 
                'method' : ['GET']
            },

            # Get design by name
            {
                'name' : 'getDesignByName',
                'path' : '%s/designsByName/:(name)' % contextRoot,
                'controller' : designController,
                'action' : 'getDesignByName', 
                'method' : ['GET']
            },

            # Add design 
            # We cannot use route like designsByName/:(name) since
            # design names can contain slashes
            {
                'name' : 'addDesign',
                'path' : '%s/designs/add' % contextRoot,
                'controller' : designSessionController,
                'action' : 'addDesign', 
                'method' : ['POST']
            },

            # Load design 
            {
                'name' : 'loadDesign',
                'path' : '%s/designs/load' % contextRoot,
                'controller' : designSessionController,
                'action' : 'loadDesign', 
                'method' : ['POST']
            },

        ]
       
        return routes


