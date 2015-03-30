#!/usr/bin/env python

import cherrypy
from cdb.common.service.cdbController import CdbController
from cdb.cdb_web_service.impl.pdmLinkControllerImpl import PdmLinkControllerImpl

class PdmLinkController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.pdmLinkControllerImpl = PdmLinkControllerImpl()

    @cherrypy.expose
    @CdbController.execute
    def getDrawing(self, name, **kwargs):
        return self.pdmLinkControllerImpl.getDrawing(name).getFullJsonRep()

    @cherrypy.expose
    @CdbController.execute
    def getDrawingThumbnail(self, ufid, **kwargs):
        return self.pdmLinkControllerImpl.getDrawingThumbnail(ufid).getFullJsonRep()

    @cherrypy.expose
    @CdbController.execute
    def getDrawingImage(self, ufid, **kwargs):
        return self.pdmLinkControllerImpl.getDrawingImage(ufid).getFullJsonRep()

