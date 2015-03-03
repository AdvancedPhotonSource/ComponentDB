#!/usr/bin/env python

#
# Route mapper for CDB web service.
#

#######################################################################

import sys
import os

import cherrypy
from cdb.common.utility.loggingManager import LoggingManager
from cdb.common.utility.configurationManager import ConfigurationManager
from cdb.common.service.loginController import LoginController
#from fileSystemController import FileSystemController
#from fileSystemSessionController import FileSystemSessionController
from componentTypeCategoryController import ComponentTypeCategoryController
from componentTypeController import ComponentTypeController
from componentController import ComponentController
from userInfoController import UserInfoController

#######################################################################

class CdbWebServiceRouteMapper:

    @classmethod
    def setupRoutes(cls):
        """ Setup RESTFul routes. """
        logger = LoggingManager.getInstance().getLogger(CdbWebServiceRouteMapper.__name__)
        contextRoot = ConfigurationManager.getInstance().getContextRoot()
        logger.debug('Using context root: %s' % contextRoot)

        # Static instances shared between different routes
        #fileSystemController = FileSystemController()
        #fileSystemSessionController = FileSystemSessionController()
        userInfoController = UserInfoController()
        componentTypeCategoryController = ComponentTypeCategoryController()
        componentTypeController = ComponentTypeController()
        componentController = ComponentController()
        loginController = LoginController()

        # Define routes.
        # Make sure to have leading '/' for consistency.
        routes = [

            # Authorization
            {
                'name'          : 'login',
                'path'          : '%s/login' % contextRoot,
                'controller'    : loginController,
                'action'        : 'login',
                'method'        : ['GET', 'PUT', 'POST', 'DELETE']
            },

            # Get user info list
            {
                'name' : 'getUserInfoList',
                'path' : '%s/users' % contextRoot,
                'controller' : userInfoController,
                'action' : 'getUserInfoList', 
                'method' : ['GET']
            },

            # Get user by id
            {
                'name' : 'getUserInfoById',
                'path' : '%s/users/:(id)' % contextRoot,
                'controller' : userInfoController,
                'action' : 'getUserInfoById', 
                'method' : ['GET']
            },

            # Get user by username
            {
                'name' : 'getUserInfoByUsername',
                'path' : '%s/usersByUsername/:(username)' % contextRoot,
                'controller' : userInfoController,
                'action' : 'getUserInfoByUsername', 
                'method' : ['GET']
            },

            # Get component type category list
            {
                'name' : 'getComponentTypeCategoryList',
                'path' : '%s/componentTypeCategories' % contextRoot,
                'controller' : componentTypeCategoryController,
                'action' : 'getComponentTypeCategoryList', 
                'method' : ['GET']
            },

            # Get component type list
            {
                'name' : 'getComponentTypeList',
                'path' : '%s/componentTypes' % contextRoot,
                'controller' : componentTypeController,
                'action' : 'getComponentTypeList', 
                'method' : ['GET']
            },

            # Get component list
            {
                'name' : 'getComponentList',
                'path' : '%s/components' % contextRoot,
                'controller' : componentController,
                'action' : 'getComponentList', 
                'method' : ['GET']
            },

            # Get directory listing (example method)
            #{
            #    'name' : 'getDirectoryList',
            #    'path' : '%s/directories/:(directoryName)' % contextRoot,
            #    'controller' : fileSystemController,
            #    'action' : 'getDirectoryList', 
            #    'method' : ['GET', 'PUT', 'POST']
            #},

            # Write file: requires login (example method)
            #{
            #    'name' : 'writeFile',
            #    'path' : '%s/files/:(fileName)' % contextRoot,
            #    'controller' : fileSystemSessionController,
            #    'action' : 'writeFile', 
            #    'method' : ['POST']
            #},
        ]
       
        # Add routes to dispatcher. 
        d = cherrypy.dispatch.RoutesDispatcher()
        for route in routes:
            logger.debug('Connecting route: %s' % route)
            d.connect(route['name'], route['path'], action=route['action'], controller=route['controller'], conditions=dict(method=route['method']))
        return d


