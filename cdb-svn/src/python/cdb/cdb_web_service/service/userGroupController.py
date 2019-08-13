#!/usr/bin/env python

import cherrypy
from cdb.common.service.cdbController import CdbController
from cdb.cdb_web_service.impl.userGroupControllerImpl import UserGroupControllerImpl

class UserGroupController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.userGroupControllerImpl = UserGroupControllerImpl()

    @cherrypy.expose
    @CdbController.execute
    def getUserGroups(self, **kwargs):
        return self.listToJson(self.userGroupControllerImpl.getUserGroups())


