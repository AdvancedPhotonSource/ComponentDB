#!/usr/bin/env python

#######################################################################

import cherrypy
import json

from cdb.common.service.cdbController import CdbController
from cdb.common.objects.cdbObject import CdbObject
from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.internalError import InternalError

from cdb.cdb_web_service.impl.userGroupControllerImpl import UserGroupControllerImpl

#######################################################################

class UserGroupController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.userGroupControllerImpl = UserGroupControllerImpl()

    @cherrypy.expose
    def getUserGroups(self, **kwargs):
        try:
           response = '%s' % self.toJson(self.userGroupControllerImpl.getUserGroups())
        except CdbException, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = ex.getFullJsonRep()
        except Exception, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = InternalError(ex).getFullJsonRep()
        return self.formatJsonResponse(response)


