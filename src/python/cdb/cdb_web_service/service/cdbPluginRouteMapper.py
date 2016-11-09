#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
import importlib
import os

from cdb.common.utility.loggingManager import LoggingManager


class CdbPluginRouteMapper:

    PLUGINS_DIR = '../plugins'
    PLUGIN_PACKAGE = 'cdb.cdb_web_service.plugins'
    ROUTE_DESCRIPTOR_NAME = 'routeDescriptor'

    @classmethod
    def getPluginRoutes(cls):
            """
            Dynamically loads routes from plugins that have been loaded into web service.

            Expected directory structure: cdb/cdb_web_service/plugins/[pluginName]/
            Plugin package must contain routeDescriptor with class Route descriptor & classmethod getRoutes().

            :return: routes for all successfully imported plugins.
            """
            pluginImports = cls.__getPluginImports()
            pluginRoutes = []
            if pluginImports is not None:
                logger = LoggingManager.getInstance().getLogger(cls.__name__)
                for pluginImport in pluginImports:
                    logger.debug("Importing routes from: %s" % pluginImport)
                    try:
                        plugin = importlib.import_module(pluginImport)
                        pluginRoutes += plugin.RouteDescriptor.getRoutes()
                    except ImportError as ex:
                        logger.error("Could not import %s: " % pluginImport)
                        logger.error(ex.message)
                    except AttributeError as ex:
                        logger.error("Could not load routes from: %s" % pluginImport)
                        logger.error(ex.message)

            return pluginRoutes

    @classmethod
    def __getPluginImports(cls):
        if os.path.exists(cls.__get_plugins_dir()):
            pluginDirectoryListing = os.listdir(cls.__get_plugins_dir())
            pluginDirectories = []
            for directoryListing in pluginDirectoryListing:
                fullPath = "%s/%s" % (cls.__get_plugins_dir(), directoryListing)
                dir = os.path.dirname(__file__)
                fullPath = os.path.join(dir, fullPath)
                if os.path.isdir(fullPath):
                    pluginDirectories.append(directoryListing)

            resultedImports = []
            for pluginDirectory in pluginDirectories:
                importStatement = '%s.%s.%s' % (cls.PLUGIN_PACKAGE, pluginDirectory, cls.ROUTE_DESCRIPTOR_NAME)
                resultedImports.append(importStatement)
            return resultedImports

        # Plugins path does not exist.
        return None

    @classmethod
    def __get_plugins_dir(cls):
        dir = os.path.dirname(__file__)
        return os.path.join(dir, cls.PLUGINS_DIR)


if __name__ == '__main__':
    print CdbPluginRouteMapper.getPluginRoutes()

