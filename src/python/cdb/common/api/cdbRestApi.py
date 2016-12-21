#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
import os
import socket
import json

from cdb.common.client.sessionManager import SessionManager
from cdb.common.utility.configurationManager import ConfigurationManager 
from cdb.common.exceptions.authorizationError import AuthorizationError
from cdb.common.api.cdbApi import CdbApi
from cdb.common.utility.urllibFileStreamUtility import UrlLibFileStreamUtility


class CdbRestApi(CdbApi):
    """ Base cdb REST api class. """

    def __init__(self, username=None, password=None, host=None, port=None, protocol=None):
        CdbApi.__init__(self)
        self.configurationManager = ConfigurationManager.getInstance()
        if username == None and password == None:
            username = self.configurationManager.getServiceUsername()
            password = self.configurationManager.getServicePassword()
        if host == None:
            host = self.configurationManager.getServiceHost()
        if port == None:
            port = self.configurationManager.getServicePort()
        if protocol == None:
            protocol = self.configurationManager.getServiceProtocol()
        self.username = username
        self.password = password
        self.host = host
        self.port = port
        self.protocol = protocol
        self.sessionManager = None

    @classmethod
    def toJson(cls, o):
        return json.dumps(o)

    @classmethod
    def fromJson(cls, s):
        return json.loads(s)

    def __getWebServiceUrl(self, url):
        if url.find('://') < 0:
            return '%s://%s:%s' % (self.protocol, socket.gethostbyname(self.host), self.port)

        # Break the URL down into component pieces
        from urlparse import urlparse
        o = urlparse(url)
        wsUrl = '%s://%s' % (o[0], o[1])
        return wsUrl

    def getContextRoot(self):
        return self.configurationManager.getContextRoot()

    def setUsername(self, username):
        self.username = username

    def getUsername(self):
        return self.username

    def setPassword(self, password):
        self.password = password

    def getPassword(self):
        return self.password

    def setHost(self, host):
        self.host = host

    def getHost(self):
        return self.host

    def setPort(self, port):
        self.port = port

    def getPort(self):
        return self.port

    def setProtocol(self, protocol):
        self.protocol = protocol

    def getProtocol(self):
        return self.protocol

    def getSessionManager(self):
        if not self.sessionManager:
            self.sessionManager = SessionManager()
        return self.sessionManager

    def getConfigManager(self):
        return self.configurationManager

    def sendSessionRequest(self, url, method, contentType='html', data={}):
        """ Send authorized session request. """
        sm = self.getSessionManager()
        if not sm.hasSession():
            if self.username == None:
                raise AuthorizationError('Username not supplied.')
            if self.password == None:
                raise AuthorizationError('Password not supplied.')
            wsUrl = self.__getWebServiceUrl(url)
            # establishSession() sets the 'wsUrl' so the explicit call
            # to setHost() is not required
            sm.establishSession(wsUrl, self.username, self.password)
        (response, responseData) = sm.sendSessionRequest(url, method, contentType, data)
        return json.loads(responseData)

    def sendRequest(self, url, method, contentType='html', data={}):
        """ Send non-authorized request. """
        sm = self.getSessionManager()
        # Because there's no call to establishSession(), explicitly call
        # setHost()
        sm.setHost(self.__getWebServiceUrl(url))
        (response, responseData) = self.getSessionManager().sendRequest(url, method, contentType, data)
        return json.loads(responseData)

    def __getFilePath(self, filePathGiven):
        """
        Generates a full path to a file name provided by the user.

        :param filePathGiven: file name provided by the user
        :return: full path to the file
        """
        if filePathGiven.startswith('/'):
            # Full path given
            return filePathGiven
        elif filePathGiven.startswith('~'):
            # Remove the home symbol
            filePathGiven = filePathGiven[1:]
            return "%s%s" % (os.path.expanduser('~'), filePathGiven)

        # Get full path
        return "%s/%s" % (os.getcwd(), filePathGiven)

    def __getFileNameFromPath(self, filepath):
        return os.path.basename(filepath)

    def _generateFileData(self, filePathProvided):
        """
        Generates a file name and data that could be used to send to server.

        :param filePathProvided:
        :return: fileName, dataObject
        """
        fullPath = self.__getFilePath(filePathProvided)
        fileName = self.__getFileNameFromPath(fullPath)
        fileDataStream = UrlLibFileStreamUtility.getStreamDataObject(fullPath)

        return fileName, fileDataStream

    def _appendUrlParameter(self, url, paramName, paramValue):
        """
        Adds a url parameter with a correct symbol (?/&).

        :param url:
        :param paramName:
        :param paramValue:
        :return: new url with parameter added
        """
        if '?' in url:
            return '%s&%s=%s' % (url, paramName, paramValue)
        else:
            return '%s?%s=%s' % (url, paramName, paramValue)

#######################################################################
# Testing.

if __name__ == '__main__':
    api = CdbRestApi('sveseli', 'sveseli')
    #api.sendRequest('https://zagreb.svdev.net:10232/cdb/directory/list', 'GET', data='')
    import urllib
    from cdb.common.utility.configurationManager import ConfigurationManager
    cm = ConfigurationManager.getInstance()
    cm.setSessionCacheFile('/tmp/session')
    #print 'Non-session request'
    #print api.sendRequest('https://zagreb.svdev.net:10232/cdb/directory/list?path=/tmp', 'GET')
    print 'Session request'
    data = { 'path' : '/tmp/xyz' }
    #print api.sendSessionRequest('https://zagreb.svdev.net:10232/cdb/file/write?path=/tmp/xyz&content=xyz', 'POST', contentType='application/x-www-form-urlencoded', data=urllib.urlencode(data))
    #print api.sendSessionRequest('https://zagreb.svdev.net:10232/cdb/file/write', 'POST', data=data)
    postdata='path=/tmp/xyz'
    postdata+='&content=%s' % urllib.quote_plus('Hey there')
    print api.sendSessionRequest('https://zagreb.svdev.net:10232/cdb/file/write', 'POST', contentType='application/x-www-form-urlencoded', data=postdata)



