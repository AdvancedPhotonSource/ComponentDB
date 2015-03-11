#!/usr/bin/env python

#
# Route mapper for CDB web service.
#

import sys
import os

import cherrypy
from cdb.common.utility.loggingManager import LoggingManager
from cdb.common.utility.configurationManager import ConfigurationManager
from loginRouteDescriptor import LoginRouteDescriptor
from userRouteDescriptor import UserRouteDescriptor
from componentRouteDescriptor import ComponentRouteDescriptor
from designRouteDescriptor import DesignRouteDescriptor

class CdbWebServiceRouteMapper:

    @classmethod
    def setupRoutes(cls):
        """ Setup RESTFul routes. """
        logger = LoggingManager.getInstance().getLogger(cls.__name__)
        contextRoot = ConfigurationManager.getInstance().getContextRoot()
        logger.debug('Using context root: %s' % contextRoot)

        # Get routes.
        routes = LoginRouteDescriptor.getRoutes() + UserRouteDescriptor.getRoutes() + ComponentRouteDescriptor.getRoutes() + DesignRouteDescriptor.getRoutes()

        # Add routes to dispatcher. 
        d = cherrypy.dispatch.RoutesDispatcher()
        for route in routes:
            logger.debug('Connecting route: %s' % route)
            d.connect(route['name'], route['path'], action=route['action'], controller=route['controller'], conditions=dict(method=route['method']))
        return d


