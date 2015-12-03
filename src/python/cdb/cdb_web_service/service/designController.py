#!/usr/bin/env python

import cherrypy
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.service.cdbController import CdbController
from cdb.cdb_web_service.impl.designControllerImpl import DesignControllerImpl

class DesignController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.designControllerImpl = DesignControllerImpl()

    @cherrypy.expose
    @CdbController.execute
    def getDesigns(self, **kwargs):
        return self.listToJson(self.designControllerImpl.getDesigns())

    @cherrypy.expose
    @CdbController.execute
    def getDesignById(self, id, **kwargs):
        if not id:
            raise InvalidRequest('Invalid id provided.')
        return self.designControllerImpl.getDesignById(id).getFullJsonRep()

    @cherrypy.expose
    @CdbController.execute
    def getDesignByName(self, name, **kwargs):
        if not name:
            raise InvalidRequest('Invalid name provided.')
        return self.designControllerImpl.getDesignByName(name).getFullJsonRep()

    @cherrypy.expose
    @CdbController.execute
    def getDesignElementById(self, id, **kwargs):
        if not id:
            raise InvalidRequest('Invalid id provided.')
        return self.designControllerImpl.getDesignElementById(id).getFullJsonRep()

