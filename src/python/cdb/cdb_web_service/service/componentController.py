#!/usr/bin/env python

import cherrypy
from cdb.common.service.cdbController import CdbController
from cdb.cdb_web_service.impl.componentControllerImpl import ComponentControllerImpl

class ComponentController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.componentControllerImpl = ComponentControllerImpl()

    @cherrypy.expose
    @CdbController.execute
    def getComponents(self, **kwargs):
        return self.listToJson(self.componentControllerImpl.getComponents())

    @cherrypy.expose
    @CdbController.execute
    def getComponentById(self, id, **kwargs):
        if not id:
            raise InvalidRequest('Invalid id provided.')
        return self.componentControllerImpl.getComponentById(id).getFullJsonRep()

    @cherrypy.expose
    @CdbController.execute
    def getComponentByName(self, name, **kwargs):
        if not name:
            raise InvalidRequest('Invalid name provided.')
        return self.componentControllerImpl.getComponentByName(name).getFullJsonRep()

