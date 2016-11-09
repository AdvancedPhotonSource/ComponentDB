#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import cherrypy
from cdb.common.service.cdbController import CdbController
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.cdb_web_service.impl.itemElementControllerImpl import ItemElementControllerImpl

class ItemElementController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.itemElementControllerImpl = ItemElementControllerImpl()

    @cherrypy.expose
    @CdbController.execute
    def getItemElementById(self, itemElementId):
        if not itemElementId:
            raise InvalidRequest("Invalid item element id provided")
        response = self.itemElementControllerImpl.getItemElementById(itemElementId).getFullJsonRep()
        self.logger.debug('Returning user info for %s: %s' % (itemElementId, response))
        return response