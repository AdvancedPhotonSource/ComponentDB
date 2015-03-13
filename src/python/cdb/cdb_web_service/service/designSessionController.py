#!/usr/bin/env python


import cherrypy

from cdb.common.service.cdbSessionController import CdbSessionController
from cdb.common.objects.cdbObject import CdbObject
from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.internalError import InternalError
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.utility.encoder import Encoder

from cdb.cdb_web_service.impl.designControllerImpl import DesignControllerImpl


class DesignSessionController(CdbSessionController):

    def __init__(self):
        CdbSessionController.__init__(self)
        self.designControllerImpl = DesignControllerImpl()

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    def addDesign(self, **kwargs):
        try:
            if not kwargs.has_key('name'):
                raise InvalidRequest('Missing design name.')
            name = Encoder.decode(kwargs.get('name'))

            sessionUser = self.getSessionUser()
            createdByUserId = sessionUser.get('id')
            ownerUserId = kwargs.get('ownerUserId', createdByUserId)
            ownerGroupId = kwargs.get('ownerGroupId')
            if ownerGroupId is None:
                ownerGroup = sessionUser.getDefaultUserGroup()
                if ownerGroup is not None:
                    ownerGroupId = ownerGroup.get('id')
            isGroupWriteable = kwargs.get('isGroupWriteable', False)
            description = kwargs.get('description')
            if description is not None:
                description = Encoder.decode(description)

            response = '%s' % self.designControllerImpl.addDesign(name, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description).getFullJsonRep()
        except CdbException, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = ex.getFullJsonRep()
        except Exception, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = InternalError(ex).getFullJsonRep()
        return self.formatJsonResponse(response)

