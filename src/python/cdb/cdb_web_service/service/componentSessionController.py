#!/usr/bin/env python

import cherrypy

from cdb.common.service.cdbSessionController import CdbSessionController
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.utility.encoder import Encoder
from cdb.common.utility.valueUtility import ValueUtility
from cdb.cdb_web_service.impl.componentControllerImpl import ComponentControllerImpl

class ComponentSessionController(CdbSessionController):

    def __init__(self):
        CdbSessionController.__init__(self)
        self.componentControllerImpl = ComponentControllerImpl()

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def addComponent(self, **kwargs):
        if not kwargs.has_key('name'):
            raise InvalidRequest('Missing component name.')
        name = Encoder.decode(kwargs.get('name'))
        if not kwargs.has_key('componentTypeId'):
            raise InvalidRequest('Missing component type id.')
        componentTypeId = kwargs.get('componentTypeId')
        modelNumber = kwargs.get('modelNumber')
        if modelNumber:
            modelNumber = Encoder.decode(modelNumber)

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

        return self.componentControllerImpl.addComponent(name, modelNumber, componentTypeId, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description).getFullJsonRep()

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def addComponentProperty(self, componentId, propertyTypeId, **kwargs):
        tag = Encoder.decode(kwargs.get('tag'))
        units = Encoder.decode(kwargs.get('units'))
        value = Encoder.decode(kwargs.get('value'))
        description  = Encoder.decode(kwargs.get('description'))
        isDynamic = ValueUtility.toBoolean(kwargs.get('isDynamic', False))
        isUserWriteable = ValueUtility.toBoolean(kwargs.get('isUserWriteable', False))

        sessionUser = self.getSessionUser()
        enteredByUserId = sessionUser.get('id')

        return self.componentControllerImpl.addComponentProperty(componentId, propertyTypeId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable).getFullJsonRep()

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def addComponentInstanceProperty(self, componentInstanceId, propertyTypeId, **kwargs):
        tag = Encoder.decode(kwargs.get('tag'))
        units = Encoder.decode(kwargs.get('units'))
        value = Encoder.decode(kwargs.get('value'))
        description  = Encoder.decode(kwargs.get('description'))
        isDynamic = ValueUtility.toBoolean(kwargs.get('isDynamic', False))
        isUserWriteable = ValueUtility.toBoolean(kwargs.get('isUserWriteable', False))

        sessionUser = self.getSessionUser()
        enteredByUserId = sessionUser.get('id')

        return self.componentControllerImpl.addComponentInstanceProperty(componentInstanceId, propertyTypeId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable).getFullJsonRep()

