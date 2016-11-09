#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Route mapper for CDB web service.
#

import cherrypy
from cdb.common.utility.configurationManager import ConfigurationManager
from cdb.common.utility.loggingManager import LoggingManager
from cdb.cdb_web_service.service.cdbPluginRouteMapper import CdbPluginRouteMapper
from itemRouteDescriptor import ItemRouteDescriptor
from loginRouteDescriptor import LoginRouteDescriptor
from userRouteDescriptor import UserRouteDescriptor


class CdbWebServiceRouteMapper:

    @classmethod
    def setupRoutes(cls):
        """ Setup RESTFul routes. """
        logger = LoggingManager.getInstance().getLogger(cls.__name__)
        contextRoot = ConfigurationManager.getInstance().getContextRoot()
        logger.debug('Using context root: %s' % contextRoot)

        # Get routes.
        routes = LoginRouteDescriptor.getRoutes() + UserRouteDescriptor.getRoutes()
        routes += CdbPluginRouteMapper.getPluginRoutes()
        routes += ItemRouteDescriptor.getRoutes()

        # Add routes to dispatcher. 
        d = cherrypy.dispatch.RoutesDispatcher()
        for route in routes:
            logger.debug('Connecting route: %s' % route)
            d.connect(route['name'], route['path'], action=route['action'], controller=route['controller'], conditions=dict(method=route['method']))
        return d


