#!/usr/bin/env python

import cherrypy
from cdb.common.service.cdbController import CdbController
from cdb.cdb_web_service.impl.userInfoControllerImpl import UserInfoControllerImpl

class UserInfoController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.userInfoControllerImpl = UserInfoControllerImpl()

    @cherrypy.expose
    @CdbController.execute
    def getUsers(self, **kwargs):
        return self.listToJson(self.userInfoControllerImpl.getUsers())

    @cherrypy.expose
    @CdbController.execute
    def getUserById(self, id, **kwargs):
        if not id:
            raise InvalidRequest('Invalid id provided.')
        response = self.userInfoControllerImpl.getUserById(id).getFullJsonRep()
        self.logger.debug('Returning user info for %s: %s' % (id,response))
        return response

    @cherrypy.expose
    @CdbController.execute
    def getUserByUsername(self, username, **kwargs):
        if not len(username):
            raise InvalidRequest('Invalid username provided.')
        response = self.userInfoControllerImpl.getUserByUsername(username).getFullJsonRep()
        self.logger.debug('Returning user info for %s: %s' % (username,response))
        return response

