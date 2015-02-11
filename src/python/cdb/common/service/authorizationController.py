#!/usr/bin/env python

#
# Authorization controller class
#

#######################################################################

import cherrypy
import urllib
from cherrypy.lib import httpauth

from cdb.common.constants import cdbStatus
from cdb.common.constants import cdbRole

from cdb.common.exceptions.cdbException import CdbException 
from cdb.common.exceptions.cdbHttpError import CdbHttpError
from cdb.common.exceptions.authorizationError import AuthorizationError
from cdb.common.utility import loggingManager
from cdb.common.service.cdbController import CdbController
from cdb.common.impl import authManager

import cdbSession

#######################################################################

cherrypy.lib.sessions.CdbSession = cdbSession.CdbSession 

SESSION_USERNAME_KEY = '_cp_username'
SESSION_ROLE_KEY = 'role'

def checkCredentials(username, password):
    """ Verifies credentials for username and password."""
    logger = loggingManager.getLogger('checkCredentials')
    logger.debug('Checking credential for User: %s, Password: %s' % (username, password))
    logger.debug('Session id: %s' % cherrypy.serving.session.id)
    principal = authManager.getInstance().getAuthPrincipal(username, password)
    logger.debug('Principal: %s' % (principal))
    if principal:
        cherrypy.session[SESSION_ROLE_KEY] = principal.getRole()
        logger.debug('Successful login from user: %s (role: %s)' % (username, principal.getRole()))
    else:
        logger.debug('Login denied for user: %s' % username)
    username = cherrypy.session.get(SESSION_USERNAME_KEY, None)

    if username is not None:
        cherrypy.request.login = None
        cherrypy.session[cdbSession.INVALID_CDB_SESSION_KEY] = True
        raise AuthorizationError('Incorrect username or password.')
    return principal

def parseBasicAuthorizationHeaders(): 
    try:
        logger = loggingManager.getLogger('parseBasicAuthorizationHeader')
        username = None
        password = None
        authorization = cherrypy.request.headers['authorization']
        authorizationHeader = httpauth.parseAuthorization(authorization)
        logger.debug('Authorization header: %s' % authorizationHeader)
        if authorizationHeader['auth_scheme'] == 'basic':
            username = authorizationHeader['username']
            password = authorizationHeader['password']
            logger.debug('Got username/password from headers: %s/%s' % (username, password))
        if username and password:
            return (username, password)
        else:
            raise AuthorizationError('Username and/or password not supplied.')
    except Exception, ex:
        errorMsg = 'Could not extract username/password from authorization header: %s' % ex
        raise AuthorizationError(errorMsg)

def checkAuth(*args, **kwargs):
    """ 
    A tool that looks in config for 'auth.require'. If found and it
    is not None, a login is required and the entry is evaluated as a list of
    conditions that the user must fulfill.
    """
    logger = loggingManager.getLogger('checkAuth')
    conditions = cherrypy.request.config.get('auth.require', None)
    logger.debug('Headers: %s' % (cherrypy.request.headers))
    logger.debug('Request params: %s' % (cherrypy.request.params))
    logger.debug('Request query string: %s' % (cherrypy.request.query_string))

    method = urllib.quote(cherrypy.request.request_line.split()[0])
    params = urllib.quote(cherrypy.request.request_line.split()[1])
    logger.debug('Session: %s' % ((cherrypy.session.__dict__)))
    if conditions is not None:
        sessionId = cherrypy.serving.session.id
        sessionCache = cherrypy.session.cache
        logger.debug('Session: %s' % ((cherrypy.session.__dict__)))
        logger.debug('Session cache length: %s' % (len(sessionCache)))
        logger.debug('Session cache: %s' % (sessionCache))
        # Check session.
        if not sessionCache.has_key(sessionId):
            errorMsg = 'Invalid or expired session id: %s.' % sessionId
            logger.debug(errorMsg)
            raise CdbHttpError(cdbHttpStatus.CDB_HTTP_UNAUTHORIZED, 'User Not Authorized', AuthorizationError(errorMsg))

        username = cherrypy.session.get(SESSION_USERNAME_KEY)
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

# Add before_handler for authorization
cherrypy.tools.auth = cherrypy.Tool('before_handler', checkAuth)
#cherrypy.tools.auth = cherrypy.Tool('on_start_resource', checkAuth)

def require(*conditions):
    """
    A decorator that appends conditions to the auth.require config variable.
    """
    def decorate(f):
        if not hasattr(f, '_cp_config'):
            f._cp_config = dict()
        if 'auth.require' not in f._cp_config:
            f._cp_config['auth.require'] = []
        f._cp_config['auth.require'].extend(conditions)
        return f
    return decorate

# Conditions are callables that return True if the user 
# fulfills the conditions they define, False otherwise.
# They can access the current username as cherrypy.request.login
def isAcdbinRole():
    return (cherrypy.session.get(SESSION_ROLE_KEY, None) == cdbRole.CDB_ACDBIN_ROLE)

def isUserOrAcdbinRole():
    role = cherrypy.session.get(SESSION_ROLE_KEY, None)
    if role == cdbRole.CDB_ACDBIN_ROLE or role == cdbRole.CDB_USER_ROLE:
        return True
    return False

def isUser(username):
    result = (cherrypy.session.get(SESSION_USERNAME_KEY, None) == username)
    return result

def getSessionUser():
    return cherrypy.session.get(SESSION_USERNAME_KEY, None)

def memberOf(groupname):
    return cherrypy.request.login == 'cdb' and groupname == 'acdbin'

def nameIs(reqd_username):
    return lambda: reqd_username == cherrypy.request.login

def anyOf(*conditions):
    """ Returns True if any of the conditions match. """
    def check():
        for c in conditions:
            if c():
                return True
        return False
    return check

def allOf(*conditions):
    """ Returns True if all of the conditions match. """
    def check():
        for c in conditions:
            if not c():
                return False
        return True
    return check

class AuthController(CdbController):
    """ Controller to provide login and logout actions. """
    _cp_config = {
        'tools.sessions.on' : True,
        'tools.sessions.storage_type' : 'cdb',
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

    @cherrypy.expose
    def login(self, username=None, password=None, fromPage='/'):
        logger = loggingManager.getLogger('login')
        try: 
            if username is None or password is None:
                (username, password) = parseBasicAuthorizationHeaders()
            principal = checkCredentials(username, password)
        except CdbHttpError, ex:
            raise
        except CdbException, ex:
            logger.debug('Authorization failed (username %s): %s' % (username, ex))
            self.addCdbExceptionHeaders(ex)
            raise CdbHttpError(cdbHttpStatus.CDB_HTTP_UNAUTHORIZED, 'User Not Authorized', ex)

        # Authorization worked.
        cherrypy.session[SESSION_USERNAME_KEY] = cherrypy.request.login = username
        self.onLogin(username)
        self.addCdbSessionRoleHeaders(principal.getRole())
        self.addCdbResponseHeaders()

    @cherrypy.expose
    def logout(self, fromPage='/'):
        sess = cherrypy.session
        username = sess.get(SESSION_USERNAME_KEY, None)
        if username:
            del sess[SESSION_USERNAME_KEY]
            cherrypy.request.login = None
            self.onLogout(username)
