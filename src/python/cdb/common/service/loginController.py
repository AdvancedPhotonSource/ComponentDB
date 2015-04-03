#!/usr/bin/env python

import cherrypy
import urllib
from cherrypy.lib import httpauth

from cdb.common.constants import cdbStatus
from cdb.common.constants import cdbRole
from cdb.common.constants import cdbHttpStatus
from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.cdbHttpError import CdbHttpError
from cdb.common.exceptions.authorizationError import AuthorizationError
from cdb.common.utility.loggingManager import LoggingManager
from cdb.common.service.cdbController import CdbController
from cdb.common.impl.authorizationManager import AuthorizationManager

class LoginController(CdbController):
    """ Controller to provide login and logout actions. """

    SESSION_USERNAME_KEY = '_cp_username'
    SESSION_USER_KEY = 'user'
    SESSION_ROLE_KEY = 'role'
    INVALID_SESSION_KEY = 'invalidSession'

    _cp_config = {
        'tools.sessions.on' : True,
        'tools.auth.on' : True
    }

    def __init__(self):
        CdbController.__init__(self)

    def onLogin(self, username):
        """ Called on successful login. """
        return

    def onLogout(self, username):
        """ Called on logout. """
        return

    @classmethod
    def getLoginForm(cls, msg='Enter username and password:', username='', fromPage='/'):
        return """
        <html>
            <body>
                <form method='post' action='/auth/login'>
                    <input type='hidden' name='fromPage' value='%(fromPage)s' />
                    <h2>CDB Service</h2>
                    <p/>
                    %(msg)s
                    <p/>
                    <table border='0'>
                        <tr>
                            <td>Username:</td><td><input type='text' name="username" value='%(username)s'/></td>
                        </tr>
                        <tr>
                            <td>Password:</td><td><input type='password' name='password' /></td>
                        </tr>
                        <p/>
                        <tr>
                            <td></td>
                            <td><input type='submit' value='Log In' /></td>
                        </tr>
                    </table>
                </form>
            </body>
        </html>""" % locals()

    @classmethod
    def parseBasicAuthorizationHeaders(cls):
        try:
            username = None
            password = None
            authorization = cherrypy.request.headers['authorization']
            authorizationHeader = httpauth.parseAuthorization(authorization)
            if authorizationHeader['auth_scheme'] == 'basic':
                username = authorizationHeader['username']
                password = authorizationHeader['password']
            if username and password:
                return (username, password)
            else:
                raise AuthorizationError('Username and/or password not supplied.')
        except Exception, ex:
            errorMsg = 'Could not extract username/password from authorization header: %s' % ex
            raise AuthorizationError(errorMsg)

    @classmethod
    def checkCredentials(cls, username, password):
        """ Verifies credentials for username and password."""
        logger = LoggingManager.getInstance().getLogger('LoginController:checkCredentials')
        logger.debug('Checking credential for User: %s' % (username))
        #logger.debug('Checking credential for User: %s, Password: %s' % (username, password))
        logger.debug('Session id: %s' % cherrypy.serving.session.id)
        principal = AuthorizationManager.getInstance().getAuthorizationPrincipal(username, password)
        logger.debug('Principal: %s' % (principal))
        if principal:
            cherrypy.session[LoginController.SESSION_ROLE_KEY] = principal.getRole()
            logger.debug('Successful login from user: %s (role: %s)' % (username, principal.getRole()))
        else:
            logger.debug('Login denied for user: %s' % username)
            username = cherrypy.session.get(LoginController.SESSION_USERNAME_KEY, None)
            if username is not None:
                cherrypy.request.login = None
                cherrypy.session[LoginController.INVALID_CDB_SESSION_KEY] = True
            raise AuthorizationError('Incorrect username or password.')
        cherrypy.session[LoginController.SESSION_USER_KEY] = principal.getUserInfo()
        return principal

    @classmethod
    def checkAuthorization(cls, *args, **kwargs):
        """
        A tool that looks in config for 'auth.require'. If found and it
        is not None, a login is required and the entry is evaluated as a list of
        conditions that the user must fulfill.
        """
        logger = LoggingManager.getInstance().getLogger('LoginController:checkAuthorization')
        conditions = cherrypy.request.config.get('auth.require', None)
        #logger.debug('Headers: %s' % (cherrypy.request.headers))
        #logger.debug('Request params: %s' % (cherrypy.request.params))
        #logger.debug('Request query string: %s' % (cherrypy.request.query_string))
        method = urllib.quote(cherrypy.request.request_line.split()[0])
        params = urllib.quote(cherrypy.request.request_line.split()[1])

        if conditions is None:
            logger.debug('No conditions imposed')
            return

        sessionId = cherrypy.serving.session.id
        sessionCache = cherrypy.session.cache
        #logger.debug('Session: %s' % ((cherrypy.session.__dict__)))
        #logger.debug('Session cache length: %s' % (len(sessionCache)))
        #logger.debug('Session cache: %s' % (sessionCache))

        # Check session.
        if not sessionCache.has_key(sessionId):
            errorMsg = 'Invalid or expired session id: %s.' % sessionId
            logger.debug(errorMsg)
            raise CdbHttpError(cdbHttpStatus.CDB_HTTP_UNAUTHORIZED, 'User Not Authorized', AuthorizationError(errorMsg))

        username = cherrypy.session.get(LoginController.SESSION_USERNAME_KEY)
        logger.debug('Session id %s is valid (username: %s)' % (sessionId, username))
        if username:
            cherrypy.request.login = username
            for condition in conditions:
                # A condition is just a callable that returns true or false
                if not condition():
                    logger.debug('Authorization check %s failed for username %s' % (condition.func_name, username))
                    errorMsg = 'Authorization check %s failed for user %s.' % (condition.func_name, username)
                    raise CdbHttpError(cdbHttpStatus.CDB_HTTP_UNAUTHORIZED, 'User Not Authorized', AuthorizationError(errorMsg))
        else:
            logger.debug('Username is not supplied')
            raise CdbHttpError(cdbHttpStatus.CDB_HTTP_UNAUTHORIZED, 'User Not Authorized', ex)


    @cherrypy.expose
    def login(self, username=None, password=None, fromPage='/'):
        self.logger.debug('Attempting login from username %s' % (username))
        try:
            if username is None or password is None:
                self.logger.debug('Parsing auth headers for username %s' % (username))
                (username, password) = LoginController.parseBasicAuthorizationHeaders()
                self.logger.debug('Retrieving principal for username %s' % (username))
            principal = LoginController.checkCredentials(username, password)
        except CdbHttpError, ex:
            raise
        except CdbException, ex:
            self.logger.debug('Authorization failed (username %s): %s' % (username, ex))
            self.addCdbExceptionHeaders(ex)
            raise CdbHttpError(cdbHttpStatus.CDB_HTTP_UNAUTHORIZED, 'User Not Authorized', ex)

        # Authorization worked.
        cherrypy.session[LoginController.SESSION_USERNAME_KEY] = cherrypy.request.login = username
        self.onLogin(username)
        self.addCdbSessionRoleHeaders(principal.getRole())
        self.addCdbResponseHeaders()

    @cherrypy.expose
    def logout(self, fromPage='/'):
        sess = cherrypy.session
        username = sess.get(LoginController.SESSION_USERNAME_KEY, None)
        if username:
            del sess[LoginController.SESSION_USERNAME_KEY]
            cherrypy.request.login = None
        self.onLogout(username)

