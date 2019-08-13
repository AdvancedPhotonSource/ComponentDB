#!/usr/bin/env python

#
# Login route descriptor.
#

from cdb.common.utility.configurationManager import ConfigurationManager
from cdb.common.service.loginController import LoginController

class LoginRouteDescriptor:

    @classmethod
    def getRoutes(cls):
        contextRoot = ConfigurationManager.getInstance().getContextRoot()

        # Static instances shared between different routes
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

        ]
       
        return routes


