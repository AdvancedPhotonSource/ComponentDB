#!/usr/bin/env python

import cherrypy
from cdb.common.service.cdbController import CdbController
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.cdb_web_service.impl.itemControllerImpl import ItemControllerImpl

class ItemController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.itemControllerImpl = ItemControllerImpl()

    @cherrypy.expose
    @CdbController.execute
    def getItemById(self, itemId):
        if not itemId:
            raise InvalidRequest("Invalid item id provided")
        response = self.itemControllerImpl.getItemById(itemId).getFullJsonRep()
        self.logger.debug('Returning user info for %s: %s' % (itemId, response))
        return response