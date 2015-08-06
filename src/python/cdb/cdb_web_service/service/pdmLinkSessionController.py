#!/usr/bin/env python

import cherrypy
from cdb.cdb_web_service.impl.pdmLinkControllerImpl import PdmLinkControllerImpl
from cdb.common.service.cdbSessionController import CdbSessionController
from cdb.common.utility.encoder import Encoder


class PdmLinkSessionController(CdbSessionController):

    def __init__(self):
        CdbSessionController.__init__(self)
        self.pdmLinkControllerImpl = PdmLinkControllerImpl()

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def createComponent(self, drawingNumber, **kwargs):
        componentTypeId = kwargs.get('componentTypeId')
        componentTypeName = kwargs.get('componentTypeName')
        if componentTypeName is not None:
            componentTypeName = Encoder.decode(componentTypeName)
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

        return self.pdmLinkControllerImpl.createComponent(drawingNumber, createdByUserId, componentTypeId, description, ownerUserId, ownerGroupId, isGroupWriteable,componentTypeName).getFullJsonRep()