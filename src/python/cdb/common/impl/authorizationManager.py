#!/usr/bin/env python

import os

from cdb.common.exceptions.authorizationError import AuthorizationError
from cdb.common.objects.cdbObjectManager import CdbObjectManager
from cdb.common.utility.configurationManager import ConfigurationManager
from cdb.common.utility.objectCache import ObjectCache
from cdb.common.utility.cryptUtility import CryptUtility

class AuthorizationManager(CdbObjectManager):

    DEFAULT_CACHE_SIZE = 10000 # number of items
    DEFAULT_CACHE_OBJECT_LIFETIME = 3600 # seconds

    # Get singleton instance.
    @classmethod
    def getInstance(cls):
        from cdb.common.impl.authorizationManager import AuthorizationManager
        try:
            am = AuthorizationManager()
        except AuthorizationManager, ex:
            am = ex
        return am

    # Singleton instance.
    __instance = None

    def __init__(self):
        if AuthorizationManager.__instance:
            raise AuthorizationManager.__instance
        AuthorizationManager.__instance = self
        CdbObjectManager.__init__(self)
        self.configurationManager = ConfigurationManager.getInstance()
        self.authorizationPrincipalRetrieverList = []
        self.objectCache = ObjectCache(AuthorizationManager.DEFAULT_CACHE_SIZE, AuthorizationManager.DEFAULT_CACHE_OBJECT_LIFETIME)

    @classmethod
    def cryptPassword(cls, cleartext):
        """ Return crypted password.... """
        return CryptUtility.cryptPassword(cleartext)

    def addAuthorizationPrincipalRetriever(self, authorizationPrincipalRetriever):
        self.authorizationPrincipalRetrieverList.append(authorizationPrincipalRetriever)

    def addAuthorizationPrincipalRetrieverList(self, authorizationPrincipalRetrieverList):
        self.authorizationPrincipalRetrieverList = self.authorizationPrincipalRetrieverList + authorizationPrincipalRetrieverList

    def authenticatePrincipal(self, principal, password):
        self.logger.debug('Check principal: %s' % principal)
        if principal is not None:
            principalToken = principal.getToken()
            if CryptUtility.verifyPassword(password, principalToken):
                return principal
        return None

    def getAuthorizationPrincipal(self, username, password):
        """ Get principal based on a username and password """
        # First try cache.
        #self.logger.debug('Trying username %s from the cache' % username)
        principal = None
        principalTuple = self.objectCache.get(username)
        if principalTuple is not None:
            (id, principal, updateTime, expirationTime) = principalTuple
            #self.logger.debug('Got username %s from the cache' % username)
        principal = self.authenticatePrincipal(principal, password)
        if principal is not None:
            self.logger.debug('Got principal %s from the cache' % principal.getName())
            return principal

        # Try all role auth principal retrievers.
        for authorizationPrincipalRetriever in self.authorizationPrincipalRetrieverList:
            principal = authorizationPrincipalRetriever.getAuthorizationPrincipal(username)
            #self.logger.debug('Got principal %s from retriever' % principal)
            principal = self.authenticatePrincipal(principal, password)
            if principal is not None:
                self.logger.debug('Adding authorization principal %s to the cache' % principal.getName())
                self.objectCache.put(username, principal)
                return principal
        return None

    def removeAuthorizationPrincipal(self, username):
        """ Clear principal from the cache. """
        self.objectCache.remove(username)

#######################################################################
# Testing.
if __name__ == '__main__':
    am = AuthorizationManager.getInstance()
    pw = AuthorizationManager.cryptPassword('password')
    print 'Password: ', pw
    from cdb.common.objects.authorizationPrincipal import AuthorizationPrincipal
    principal = AuthorizationPrincipal('sv', pw)
    print 'Principal: ', principal
    authPrincipal = am.authenticatePrincipal(principal, 'password')
    print 'Auth Principal: ', authPrincipal

