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
from userGroupController import UserGroupController

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
        userGroupController = UserGroupController()
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

            # Get user group list
            {
                'name' : 'getUserGroups',
                'path' : '%s/userGroups' % contextRoot,
                'controller' : userGroupController,
                'action' : 'getUserGroups', 
                'method' : ['GET']
            },

            # Get user info list
            {
                'name' : 'getUsers',
                'path' : '%s/users' % contextRoot,
                'controller' : userInfoController,
                'action' : 'getUsers', 
                'method' : ['GET']
            },

            # Get user by id
            {
                'name' : 'getUserById',
                'path' : '%s/users/:(id)' % contextRoot,
                'controller' : userInfoController,
                'action' : 'getUserById', 
                'method' : ['GET']
            },

            # Get user by username
            {
                'name' : 'getUserByUsername',
                'path' : '%s/usersByUsername/:(username)' % contextRoot,
                'controller' : userInfoController,
                'action' : 'getUserByUsername', 
                'method' : ['GET']
            },

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


