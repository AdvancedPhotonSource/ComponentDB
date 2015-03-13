#!/usr/bin/env python

#######################################################################

import cherrypy
import json

from cdb.common.service.cdbController import CdbController
from cdb.common.objects.cdbObject import CdbObject
from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.internalError import InternalError

from cdb.cdb_web_service.impl.componentTypeControllerImpl import ComponentTypeControllerImpl

#######################################################################

class ComponentTypeController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.componentTypeControllerImpl = ComponentTypeControllerImpl()

    @cherrypy.expose
    def getComponentTypes(self, **kwargs):
        try:
           response = '%s' % self.toJson(self.componentTypeControllerImpl.getComponentTypes())
        except CdbException, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = ex.getFullJsonRep()
        except Exception, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = InternalError(ex).getFullJsonRep()
        return self.formatJsonResponse(response)


