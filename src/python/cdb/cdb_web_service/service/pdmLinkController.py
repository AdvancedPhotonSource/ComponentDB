#!/usr/bin/env python

#######################################################################

import cherrypy
import json

from cdb.common.service.cdbController import CdbController
from cdb.common.objects.cdbObject import CdbObject
from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.internalError import InternalError

from cdb.cdb_web_service.impl.pdmLinkControllerImpl import PdmLinkControllerImpl

#######################################################################


class PdmLinkController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.pdmLinkControllerImpl = PdmLinkControllerImpl()

    @cherrypy.expose
    def getDrawing(self, name, **kwargs):
        try:
            response = '%s' % self.pdmLinkControllerImpl.getDrawing(name).getFullJsonRep()
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
    def getDrawingThumbnail(self, ufid, **kwargs):
        try:
            response = '%s' % self.pdmLinkControllerImpl.getDrawingThumbnail(ufid).getFullJsonRep()
        except CdbException, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = ex.getJsonRep()
        except Exception, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = InternalError(ex).getJsonRep()
        return self.formatJsonResponse(response)


