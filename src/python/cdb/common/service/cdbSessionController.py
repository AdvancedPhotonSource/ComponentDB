#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Base CDB session controller class.
#

#######################################################################

import cherrypy
from cdb.common.service.cdbController import CdbController
from cdb.common.service.loginController import LoginController

#######################################################################

class CdbSessionController(CdbController):
    """ Base session controller class. """

    _cp_config = {
        'tools.sessions.on': True,
        'tools.auth.on': True
    }

    #auth = LoginController()
    # Add before_handler for authorization
    cherrypy.tools.auth = cherrypy.Tool('before_handler', LoginController.checkAuthorization)

    def __init__(self):
        CdbController.__init__(self)

    @classmethod
    def require(cls, *conditions):
        """
        Decorator that appends conditions to the auth.require config
        variable.
        """
        def decorate(f):
            if not hasattr(f, '_cp_config'):
                f._cp_config = dict()
            if 'auth.require' not in f._cp_config:
                f._cp_config['auth.require'] = []
                f._cp_config['auth.require'].extend(conditions)
            return f
        return decorate

    @classmethod
    def anyOf(cls, *conditions):
        """ Returns True if any of the conditions match. """
        def check():
            for c in conditions:
                if c():
                    return True
            return False
        return check

    @classmethod
    def allOf(cls, *conditions):
        """ Returns True if all of the conditions match. """
        def check():
            for c in conditions:
                if not c():
                    return False
            return True
        return check

    @classmethod
    def isLoggedIn(cls):
        """ Returns True if session has been established. """
        def check():
            role = cherrypy.session.get(LoginController.SESSION_ROLE_KEY, None)
            if role is not None:
                return True
            return False
        return check

