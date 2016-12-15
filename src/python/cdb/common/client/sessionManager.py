#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import urllib
import urllib2
import urlparse
import time
import getpass
import os
import stat
import ssl
import types

from cdb.common.constants import cdbServiceConstants
from cdb.common.exceptions.configurationError import ConfigurationError
from cdb.common.exceptions.authorizationError import AuthorizationError
from cdb.common.exceptions.invalidSession import InvalidSession
from cdb.common.exceptions.urlError import UrlError
from cdb.common.utility.loggingManager import LoggingManager
from cdb.common.utility.configurationManager import ConfigurationManager
from cdb.common.utility.osUtility import OsUtility
from cdb.common.utility.urllibFileStreamUtility import UrlLibFileStreamUtility
from cdb.common.client.cdbExceptionMapper import CdbExceptionMapper
from cdb.common.client.cdbHttpsHandler import CdbHttpsHandler

class SessionManager:
    """ Class for session management. """

    def __init__(self):
        self.sessionCookie = None
        self.host = None
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)
        self.username = ''
        self.password = ''
        self.urlOpener = None
        cm = ConfigurationManager.getInstance()
        self.sessionCacheFile = cm.getSessionCacheFile()
        self.requireSessionCredentials = cm.getRequireSessionCredentials()

    def setHost(self, host):
        self.host = host

    def hasSession(self):
        """ Return true if we have session established. """
        if self.sessionCookie is not None:
            return True
        return False

    def establishSession(self, url, username, password, selector='/cdb/login'):
        self.host = url
        self.username = username
        self.password = password
        self.sessionCookie = self.loadSession()
        if self.sessionCookie is not None:
            return

        # Could not load session.
        try:
            # User will be asked for username/password if they are not
            # provided.
            data = { 'username' : self.getUsername(username), 'password' : self.getPassword(password) }
            self.logger.debug('Establishing session for user %s @ %s)' % (username, url))
            (response,responseData) = self.sendRequest(url='%s%s' % (url, selector), method='POST', contentType='application/x-www-form-urlencoded', data=urllib.urlencode(data))
        except urllib2.URLError, ex:
            self.logger.exception('%s' % ex)
            raise UrlError(exception=ex)

        #self.logger.debug('Got headers: %s' % response.headers)
        # This will save session cookie.
        self.sessionCookie = self.checkResponseHeadersForErrorsAndSaveSession(response.headers)
        self.logger.debug('User %s session cookie: %s' % (username, self.sessionCookie))

    def getUsername(self, username):
        if not username and self.requireSessionCredentials:
            return self.askForUsername()
        return username

    def askForUsername(self):
        defaultUsername = getpass.getuser()
        username = raw_input('Username [%s]: ' % defaultUsername)
        username = username.strip()
        if not len(username):
            username = defaultUsername
        return username

    def getPassword(self, password):
        if not password and self.requireSessionCredentials:
            return self.askForPassword()
        return password

    def askForPassword(self):
        password = getpass.getpass()
        password = password.strip()
        if not len(password):
            raise AuthorizationError('Empty password provided.')
        return password

    def clearSessionFile(self):
        if self.sessionCacheFile is None:
            return
        try:
            self.logger.debug('Clearing session cache: %s' % (self.sessionCacheFile))
            OsUtility.removeFile(self.sessionCacheFile)
        except Exception, ex:
            # ignore errors.
            self.logger.warn('Could not clear session cache: %s' % (ex))
            pass

    def saveSession(self, sessionCookie):
        if self.sessionCacheFile is None:
            return
        if sessionCookie is None:
            return
        try:
            f = open(self.sessionCacheFile, 'w')
            f.write('%s' % sessionCookie)
            f.close()
            os.chmod(self.sessionCacheFile, stat.S_IRUSR|stat.S_IWUSR|stat.S_IXUSR)
        except Exception, ex:
            self.logger.warn('Could not save session: %s' % (ex))

    def loadSession(self):
        if self.sessionCacheFile is None:
            return None
        try:
            f = open(self.sessionCacheFile, 'r')
            session = f.read()
            expires = session.split(';')[1].split('=')[-1]
            t = time.mktime(time.strptime(expires, '%a, %d %b %Y %H:%M:%S %Z'))
            now = time.time()
            if t < now:
                return None
            else:
                self.logger.debug('Loaded session from %s: %s' % (self.sessionCacheFile, session))
                return session
        except Exception, ex:
            self.logger.warn('Could not load session: %s' % (ex))
        return None

    def getUrlOpener(self, protocol):
        if not self.urlOpener:
            if protocol == cdbServiceConstants.CDB_SERVICE_PROTOCOL_HTTPS:
                # HTTPS, use custom https handler, which 
                # should work even if any of cert/key/cacert files is None
                cm = ConfigurationManager.getInstance()
                keyFile = cm.getSslKeyFile()
                certFile = cm.getSslCertFile()
                self.urlOpener = urllib2.build_opener(CdbHttpsHandler)
                #self.logger.debug('Using Cdb HTTPS Handler')
            else:
                # HTTP, use standard http handler
                self.urlOpener = urllib2.build_opener(urllib2.HTTPHandler)
        # Install opener before returning it.
        urllib2.install_opener(self.urlOpener)
        return self.urlOpener

    def sendRequest(self, url, method, contentType='html', data={}):
        """ Send http request without cookies. """
        if url.find('://') < 0:
            url = '%s%s' % (self.host, url)
        parsedUrl = urlparse.urlparse(url)
        protocol = parsedUrl[0]
        path = parsedUrl[2]
        self.logger.debug('Sending request: %s' % url)
        encodedData = ''
        if data is not None:
            if type(data) == types.DictType and len(data):
                encodedData=urllib.urlencode(data)
                contentType='application/x-www-form-urlencoded'
            elif type(data) == types.StringType:
                encodedData = data
            elif UrlLibFileStreamUtility.isStreamDataObject(data):
                encodedData = data
                contentType = 'application/octet-stream'
                # In case data was used on invalid session
                encodedData.seek(0)

        request = urllib2.Request(url, data=encodedData)
        request.get_method = lambda: method
        request.add_header('Content-Type', contentType)
        request.add_header('Content-Length', str(len(data)))
        if self.sessionCookie != None:
            request.add_header('Cookie', self.sessionCookie)
        try:
            opener = self.getUrlOpener(protocol)
            response = opener.open(request)
        except urllib2.HTTPError, ex:
            # If we see cdb headers, cdb exception will be thrown,
            # otherwise we'll throw UrlError
            self.checkResponseHeadersForErrors(ex.hdrs)
            self.logger.exception('%s' % ex)
            raise UrlError(exception=ex)
        except urllib2.URLError, ex:
            self.logger.exception('%s' % ex)
            raise UrlError(exception=ex)

        # Check headers for errors and update session cookie
        sessionCookie = self.checkResponseHeadersForErrorsAndSaveSession(response.headers)
        if sessionCookie != None:
            self.sessionCookie = sessionCookie
        responseData = response.read()
        return (response, responseData)

    def sendSessionRequest(self, url, method, contentType='html', data={}):
        """ Send session request. """
        if self.sessionCookie is None:
            self.establishSession(self.host, self.username, self.password)
        try:
            return self.sendRequest(url, method, contentType, data)
        except InvalidSession, ex:
            self.clearSessionFile()
            self.establishSession(self.host, self.username, self.password)
            return self.sendRequest(url, method, contentType, data)

    def checkResponseHeadersForErrorsAndSaveSession(self, responseHeaders):
        try:
            CdbExceptionMapper.checkStatus(responseHeaders)
            sessionCookie = responseHeaders.get('Set-Cookie')
            self.saveSession(sessionCookie)
            return sessionCookie
        except (AuthorizationError, InvalidSession), ex:
            self.clearSessionFile()
            raise

    def checkResponseHeadersForErrors(self, responseHeaders):
        try:
            CdbExceptionMapper.checkStatus(responseHeaders)
        except AuthorizationError, ex:
            self.clearSessionFile()
            raise

#######################################################################
# Testing.
if __name__ == '__main__':
    sm = SessionManager.createSession()
