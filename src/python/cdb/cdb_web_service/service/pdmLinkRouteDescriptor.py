#!/usr/bin/env python

#
# PdmLink route descriptor.
#

from cdb.common.utility.configurationManager import ConfigurationManager
from pdmLinkController import PdmLinkController


class PDMLinkRouteDescriptor:

    @classmethod
    def getRoutes(cls):
        contextRoot = ConfigurationManager.getInstance().getContextRoot()

        # Static instances shared between different routes
        pdmLinkController = PdmLinkController()

        # Define routes.
        routes = [

            # Get drawing 
            {
                'name': 'getDrawing',
                'path': '%s/pdmLink/drawings/:(name)' % contextRoot,
                'controller': pdmLinkController,
                'action': 'getDrawing',
                'method': ['GET']
            },

            # Get PdmLink drawing thumbnail 
            {
                'name': 'getDrawingThumbnail',
                'path': '%s/pdmLink/drawingThumbnails/:(ufid)' % contextRoot,
                'controller': pdmLinkController,
                'action': 'getDrawingThumbnail',
                'method': ['GET']
            },

            # Get PdmLink drawing image 
            {
                'name': 'getDrawingImage',
                'path': '%s/pdmLink/drawingImages/:(ufid)' % contextRoot,
                'controller': pdmLinkController,
                'action': 'getDrawingImage',
                'method': ['GET']
            }

        ]
       
        return routes


