#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Item route descriptor.
#

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
            }

        ]

        return routes
