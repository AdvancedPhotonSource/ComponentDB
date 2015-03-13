#!/usr/bin/env python

#######################################################################

import cherrypy
import json

from cdb.common.service.cdbController import CdbController
from cdb.common.objects.cdbObject import CdbObject
from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.internalError import InternalError

from cdb.cdb_web_service.impl.pdmLinkConnectionImpl import PDMLinkConnectionImpl

#######################################################################


class PDMLinkController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.pdmLinkConnectionImpl = PDMLinkConnectionImpl()

    @cherrypy.expose
    def getDrawingRevisionsInfo(self, drawingNumber, **kwargs):
        try:
            response = '%s' % self.pdmLinkConnectionImpl.getDrawingRevisionsInfo(drawingNumber).getJsonRep('__all__')
        except CdbException, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = ex.getJsonRep()
        except Exception, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = InternalError(ex).getJsonRep()
        return self.formatJsonResponse(response)


