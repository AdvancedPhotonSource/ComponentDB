#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import cherrypy
from cdb.common.service.cdbController import CdbController
from cdb.cdb_web_service.plugins.pdmLink.impl.icmsControllerImpl import IcmsControllerImpl

class IcmsController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.icmsControllerImpl = IcmsControllerImpl()

    @cherrypy.expose
    @CdbController.execute
    def performQuickSearch(self, keyword):
        return self.icmsControllerImpl.performQuickSearch(keyword)

    @cherrypy.expose
    @CdbController.execute
    def getDocInfo(self, docName):
        return self.icmsControllerImpl.getDocInfo(docName)