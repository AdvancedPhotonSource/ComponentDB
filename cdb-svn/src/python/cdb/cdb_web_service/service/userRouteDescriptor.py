#!/usr/bin/env python

#
# User route descriptor.
#

from cdb.common.utility.configurationManager import ConfigurationManager
from userInfoController import UserInfoController
from userGroupController import UserGroupController

class UserRouteDescriptor:

    @classmethod
    def getRoutes(cls):
        contextRoot = ConfigurationManager.getInstance().getContextRoot()

        # Static instances shared between different routes
        userInfoController = UserInfoController()
        userGroupController = UserGroupController()

        # Define routes.
        routes = [

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

        ]
       
        return routes


