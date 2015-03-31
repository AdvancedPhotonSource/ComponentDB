#!/usr/bin/env python

import cherrypy
from cdb.common.service.cdbController import CdbController
from cdb.cdb_web_service.impl.componentTypeControllerImpl import ComponentTypeControllerImpl

class ComponentTypeController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.componentTypeControllerImpl = ComponentTypeControllerImpl()

    @cherrypy.expose
    @CdbController.execute
    def getComponentTypes(self, **kwargs):
        return self.listToJson(self.componentTypeControllerImpl.getComponentTypes())


