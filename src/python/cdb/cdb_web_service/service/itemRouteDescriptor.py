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
from itemElementSessionController import ItemElementSessionController


class ItemRouteDescriptor:
    @classmethod
    def getRoutes(cls):
        contextRoot = ConfigurationManager.getInstance().getContextRoot()

        # Static instances shared between different routes
        itemController = ItemController()
        itemElementController = ItemElementController()
        itemElementSessionController = ItemElementSessionController()
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

            # Get item by qrid
            {
                'name': 'getItemByQrId',
                'path': '%s/items/:(itemQrId)/qrId' % contextRoot,
                'controller': itemController,
                'action': 'getItemByQrId',
                'method': ['GET']
            },

            # Get item by unique attributes
            {
                'name': 'getItemByUniqueAttributes',
                'path': '%s/items/uniqueAttributes/:(domainName)/:(itemName)' % contextRoot,
                'controller': itemController,
                'action': 'getItemByUniqueAttributes',
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
            },

            # Add a item element relationship between two items
            {
                'name': 'addItemRelationship',
                'path': '%s/items/:(firstItemId)/:(secondItemId)/addItemElementRelationship/:(relationshipTypeName)' % contextRoot,
                'controller': itemSessionController,
                'action': 'addItemElementRelationship',
                'method': ['POST']
            },

            # Add a item element relationship between two items
            {
                'name': 'addItemRelationshipByQrId',
                'path': '%s/items/:(firstItemQrId)/:(secondItemQrId)/addItemElementRelationshipByQrId/:(relationshipTypeName)' % contextRoot,
                'controller': itemSessionController,
                'action': 'addItemElementRelationship',
                'method': ['POST']
            },

            # Get item element relationship list for first item
            {
                'name': 'getFirstItemRelationships',
                'path': '%s/items/:(itemId)/firstItemElementRelationships/:(relationshipTypeName)' % contextRoot,
                'controller': itemController,
                'action': 'getFirstItemRelationship',
                'method': ['GET']
            },

            # Get properties for a particular item
            {
                'name': 'getPropertiesForItemByItemId',
                'path': '%s/items/:(itemId)/propertiesByItemId' % contextRoot,
                'controller': itemController,
                'action': 'getPropertiesForItemByItemId',
                'method': ['GET']
            },

            # Get log entries for a particular item
            {
                'name': 'getLogEntriesForItemById',
                'path': '%s/items/:(itemId)/logsByItemId' % contextRoot,
                'controller': itemController,
                'action': 'getItemLogsById',
                'method': ['GET']
            },
            # Get item elements for a particular item
            {
                'name': 'getParentItems',
                'path': '%s/items/:(itemId)/parentItems' % contextRoot,
                'controller': itemController,
                'action': 'getParentItems',
                'method': ['GET']
            },
            # Get item elements for a particular item
            {
                'name': 'getItemElementsForItem',
                'path': '%s/items/:(itemId)/elementsByItemId' % contextRoot,
                'controller': itemController,
                'action': 'getItemElementsForItem',
                'method': ['GET']
            },

            # Get items derived from item
            {
                'name': 'getItemsDerivedFromItem',
                'path': '%s/items/derivedFromItem/:(derivedFromItemId)' % contextRoot,
                'controller': itemController,
                'action': 'getItemsDerivedFromItem',
                'method': ['GET']
            },

            # Get catalog items
            {
                'name': 'getCatalogItems',
                'path': '%s/items/domain/catalog' % contextRoot,
                'controller': itemController,
                'action': 'getCatalogItems',
                'method': ['GET']
            },

            # Get location items
            {
                'name': 'getLocationItems',
                'path': '%s/items/domain/location' % contextRoot,
                'controller': itemController,
                'action': 'getLocationItems',
                'method': ['GET']
            },

            # Get top level location items
            {
                'name': 'getLocationItemsWithoutParents',
                'path': '%s/items/domain/location/topLevel' % contextRoot,
                'controller': itemController,
                'action': 'getTopLevelLocationItems',
                'method': ['GET']
            },

            # Add Property value to item
            {
                'name': 'addPropertyValueForItemById',
                'path': '%s/items/:(itemId)/addPropertyValue/:(propertyTypeName)' % contextRoot,
                'controller': itemSessionController,
                'action': 'addPropertyValueToItemByItemId',
                'method': ['POST']
            },

            # Add Property value to item element
            {
                'name': 'addPropertyValueForItemElementById',
                'path': '%s/itemElements/:(itemElementId)/addPropertyValue/:(propertyTypeName)' % contextRoot,
                'controller': itemElementSessionController,
                'action': 'addPropertyValueToItemElementById',
                'method': ['POST']
            },

            # Get all domains
            {
                'name': 'getDomains',
                'path': '%s/domains' % contextRoot,
                'controller': itemController,
                'action': 'getDomains',
                'method': ['GET']
            },

            # Add new item
            {
                'name': 'addItem',
                'path': '%s/items/add/:(name)/domain/:(domainName)' % contextRoot,
                'controller': itemSessionController,
                'action': 'addItem',
                'method': ['POST']
            },

            # Add item element
            {
                'name': 'addItemElement',
                'path': '%s/itemElements/add/:(itemElementName)/:(parentItemId)' % contextRoot,
                'controller': itemElementSessionController,
                'action': 'addItemElement',
                'method': ['POST']
            },

            # Update item element
            {
                'name': 'updateItemElement',
                'path': '%s/itemElements/update/:(itemElementId)' % contextRoot,
                'controller': itemElementSessionController,
                'action': 'updateItemElement',
                'method': ['PUT']
            }


        ]

        return routes
