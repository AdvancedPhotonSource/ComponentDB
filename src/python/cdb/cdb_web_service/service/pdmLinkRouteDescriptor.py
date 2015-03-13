#!/usr/bin/env python

#
# PdmLink route descriptor.
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

            # Get pdm link drawing revision information
            {
                'name': 'getDrawingRevisions',
                'path': '%s/pdmLink/drawingRevisionsInfo/:(drawingNumber)' % contextRoot,
                'controller': pdmLinkController,
                'action': 'getDrawingRevisionsInfo',
                'method': ['GET']
            },
            # Get thumbnail of a revision of a PdmLink drawing
            {
                'name': 'getDrawingThumbnail',
                'path': '%s/pdmLink/drawingRevisionThumbnail/:(drawingRevUfid)' % contextRoot,
                'controller': pdmLinkController,
                'action': 'getDrawingThumbnail',
                'method': ['GET']
            }

        ]
       
        return routes


