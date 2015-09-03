#!/usr/bin/env python

import cherrypy
from cdb.common.utility.encoder import Encoder
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
    def getComponentsByName(self, name, **kwargs):
        name = Encoder.decode(name)
        return self.listToJson(self.componentControllerImpl.getComponentsByName(name))

    @cherrypy.expose
    @CdbController.execute
    def getComponentById(self, id, **kwargs):
        if not id:
            raise InvalidRequest('Invalid id provided.')
        return self.componentControllerImpl.getComponentById(id).getFullJsonRep()

    @cherrypy.expose
    @CdbController.execute
    def getComponentByName(self, name, **kwargs):
        name = Encoder.decode(name)
        if not name:
            raise InvalidRequest('Invalid name provided.')
        return self.componentControllerImpl.getComponentByName(name).getFullJsonRep()

    @cherrypy.expose
    @CdbController.execute
    def getComponentByModelNumber(self, modelNumber, **kwargs):
        modelNumber = Encoder.decode(modelNumber)
        if not modelNumber:
            raise InvalidRequest('Invalid model number provided.')
        return self.componentControllerImpl.getComponentByModelNumber(modelNumber).getFullJsonRep()

