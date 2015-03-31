#!/usr/bin/env python

import cherrypy
from cdb.common.service.cdbController import CdbController
from cdb.cdb_web_service.impl.componentTypeCategoryControllerImpl import ComponentTypeCategoryControllerImpl

class ComponentTypeCategoryController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.componentTypeCategoryControllerImpl = ComponentTypeCategoryControllerImpl()

    @cherrypy.expose
    @CdbController.execute
    def getComponentTypeCategories(self, **kwargs):
        return self.listToJson(self.componentTypeCategoryControllerImpl.getComponentTypeCategories())


