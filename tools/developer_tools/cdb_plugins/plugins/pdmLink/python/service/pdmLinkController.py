#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import cherrypy
from cdb.common.service.cdbController import CdbController
from cdb.cdb_web_service.plugins.pdmLink.impl.pdmLinkControllerImpl import PdmLinkControllerImpl

class PdmLinkController(CdbController):

    TOP_LEVEL_SEARCH_RESULTS_KEY = 'searchResults'

    def __init__(self):
        CdbController.__init__(self)
        self.pdmLinkControllerImpl = PdmLinkControllerImpl()

    @cherrypy.expose
    @CdbController.execute
    def getDrawing(self, drawingNumber, **kwargs):
        return self.pdmLinkControllerImpl.getDrawing(drawingNumber).getFullJsonRep()

    @cherrypy.expose
    @CdbController.execute
    def getDrawings(self, searchPattern, **kwargs):
        return self.listToJson(self.pdmLinkControllerImpl.getDrawings(searchPattern), topLevelKey=self.TOP_LEVEL_SEARCH_RESULTS_KEY)

    @cherrypy.expose
    @CdbController.execute
    def getDrawingSearchResults(self, searchPattern, **kwargs):
        return self.listToJson(self.pdmLinkControllerImpl.getDrawingSearchResults(searchPattern), topLevelKey=self.TOP_LEVEL_SEARCH_RESULTS_KEY)

    @cherrypy.expose
    @CdbController.execute
    def getRelatedDrawingSearchResults(self, drawingNumberBase, **kwargs):
        return self.listToJson(self.pdmLinkControllerImpl.getRelatedDrawingSearchResults(drawingNumberBase), topLevelKey=self.TOP_LEVEL_SEARCH_RESULTS_KEY)

    @cherrypy.expose
    @CdbController.execute
    def completeDrawingInformation(self, ufid, oid, **kwargs):
        return self.pdmLinkControllerImpl.completeDrawingInformation(ufid, oid).getFullJsonRep()

    @cherrypy.expose
    @CdbController.execute
    def getDrawingThumbnail(self, ufid, **kwargs):
        return self.pdmLinkControllerImpl.getDrawingThumbnail(ufid).getFullJsonRep()

    @cherrypy.expose
    @CdbController.execute
    def getDrawingImage(self, ufid, **kwargs):
        return self.pdmLinkControllerImpl.getDrawingImage(ufid).getFullJsonRep()

    @cherrypy.expose
    @CdbController.execute
    def generateComponentInfo(self, drawingNumber, **kwargs):
        return self.pdmLinkControllerImpl.generateComponentInfo(drawingNumber).getFullJsonRep()
