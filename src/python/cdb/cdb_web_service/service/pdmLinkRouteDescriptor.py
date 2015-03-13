#!/usr/bin/env python

#
# Component route descriptor.
#

from cdb.common.utility.configurationManager import ConfigurationManager
from pdmLinkController import PDMLinkController


class PDMLinkRouteDescriptor:

    @classmethod
    def getRoutes(cls):
        contextRoot = ConfigurationManager.getInstance().getContextRoot()

        # Static instances shared between different routes
        pdmLinkController = PDMLinkController()

        # Define routes.
        # Make sure to have leading '/' for consistency.
        routes = [

            # Get component type category list
            {
                'name': 'getDrawingRevisions',
                'path': '%s/pdmLink/drawingRevisionsInfo/:(drawingNumber)' % contextRoot,
                'controller': pdmLinkController,
                'action': 'getDrawingRevisionsInfo',
                'method': ['GET']
            }

        ]
       
        return routes


