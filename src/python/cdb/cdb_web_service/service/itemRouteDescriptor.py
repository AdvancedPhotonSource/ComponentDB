#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Item route descriptor.
#
from cdb.cdb_web_service.service.itemSessionController import ItemSessionController
from cdb.common.utility.configurationManager import ConfigurationManager
from itemController import ItemController
from itemElementController import ItemElementController


class ItemRouteDescriptor:
    @classmethod
    def getRoutes(cls):
        contextRoot = ConfigurationManager.getInstance().getContextRoot()

        # Static instances shared between different routes
        itemController = ItemController()
        itemElementController = ItemElementController()
        itemSessionController = ItemSessionController()

        # Define routes.
        routes = [

            # Get item by id
            {
                'name': 'getItemById',
                'path': '%s/items/:(itemId)' % contextRoot,
                'controller': itemController,
                'action': 'getItemById',
                'method': ['GET']
            },

            # Get item element by id
            {
                'name': 'getItemElementById',
                'path': '%s/itemElements/:(itemElementId)' % contextRoot,
                'controller': itemElementController,
                'action': 'getItemElementById',
                'method': ['GET']
            },

            # Add a log entry for an item
            {
                'name': 'addLogEntryForItemByQrId',
                'path': '%s/items/:(qrId)/addLogEntry' % contextRoot,
                'controller': itemSessionController,
                'action': 'addLogToItemByQrId',
                'method': ['POST']
            },

            # Get log entries for a particular item
            {
                'name': 'getLogEntriesForItemByQrId',
                'path': '%s/items/:(qrId)/logs' % contextRoot,
                'controller': itemController,
                'action': 'getItemLogsByQrId',
                'method': ['GET']
            }

        ]

        return routes
