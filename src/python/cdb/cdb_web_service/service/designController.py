#!/usr/bin/env python

import cherrypy
import json

from cdb.common.service.cdbController import CdbController
from cdb.common.objects.cdbObject import CdbObject
from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.internalError import InternalError

from cdb.cdb_web_service.impl.designControllerImpl import DesignControllerImpl

class DesignController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.designControllerImpl = DesignControllerImpl()

    @cherrypy.expose
    def getDesigns(self, **kwargs):
        try:
           response = '%s' % self.toJson(self.designControllerImpl.getDesigns())
        except CdbException, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = ex.getJsonRep()
        except Exception, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = InternalError(ex).getJsonRep()
        return self.formatJsonResponse(response)

    @cherrypy.expose
    def getDesignById(self, id, **kwargs):
        try:
            if not id:
                raise InvalidRequest('Invalid id provided.')
            response = '%s' % self.designControllerImpl.getDesignById(id).getJsonRep()
        except CdbException, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = ex.getJsonRep()
        except Exception, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = InternalError(ex).getJsonRep()
        return self.formatJsonResponse(response)

    @cherrypy.expose
    def getDesignByName(self, name, **kwargs):
        try:
            if not name:
                raise InvalidRequest('Invalid name provided.')
            response = '%s' % self.designControllerImpl.getDesignByName(name).getJsonRep()
        except CdbException, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = ex.getJsonRep()
        except Exception, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = InternalError(ex).getJsonRep()
        return self.formatJsonResponse(response)

