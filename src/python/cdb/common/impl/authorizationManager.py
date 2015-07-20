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

    CONFIG_SECTION_NAME = 'AuthorizationManager'
    ADMIN_GROUP_NAME_KEY = 'admingroupname'
    PRINCIPAL_RETRIEVER_KEY = 'principalretriever'
    PRINCIPAL_AUTHENTICATOR_KEY = 'principalauthenticator'

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
        self.principalRetriever = None
        self.principalAuthenticatorList = []
        self.objectCache = ObjectCache(AuthorizationManager.DEFAULT_CACHE_SIZE, AuthorizationManager.DEFAULT_CACHE_OBJECT_LIFETIME)
        self.adminGroupName = None
        self.configure()

    def createObjectInstance(self, moduleName, className, constructor):
        self.logger.debug('Creating object: %s, %s, %s' % (moduleName, className, constructor))
        cmd = 'from %s import %s' % (moduleName, className)
        exec cmd
        cmd = 'objectInstance = %s' % (constructor)
        exec cmd
        return objectInstance

    @classmethod
    def cryptPassword(cls, cleartext):
        return CryptUtility.cryptPassword(cleartext)

    @classmethod
    def cryptPasswordWithPbkdf2(cls, cleartext):
        return CryptUtility.cryptPasswordWithPbkdf2(cleartext)

    def getAdminGroupName(self):
        return self.adminGroupName 

    def configure(self):
        configItems = self.configurationManager.getConfigItems(AuthorizationManager.CONFIG_SECTION_NAME)
        self.logger.debug('Got config items: %s' % configItems)
        adminGroupName = self.configurationManager.getConfigOption(AuthorizationManager.CONFIG_SECTION_NAME, AuthorizationManager.ADMIN_GROUP_NAME_KEY)
        self.adminGroupName = adminGroupName 

        # Create principal retriever
        principalRetriever = self.configurationManager.getConfigOption(AuthorizationManager.CONFIG_SECTION_NAME, AuthorizationManager.PRINCIPAL_RETRIEVER_KEY)
        (moduleName,className,constructor) = self.configurationManager.getModuleClassConstructorTuple(principalRetriever)    
        self.logger.debug('Creating principal retriever class: %s' % className)
        self.principalRetriever = self.createObjectInstance(moduleName, className, constructor)
        self.principalRetriever.setAdminGroupName(adminGroupName)
        self.logger.debug('Authorization principal retriever: %s' % (self.principalRetriever))

        # Create principal authenticators
        for (key,value) in configItems:
            if key.startswith(AuthorizationManager.PRINCIPAL_AUTHENTICATOR_KEY):
                (moduleName,className,constructor) = self.configurationManager.getModuleClassConstructorTuple(value)    
                self.logger.debug('Creating principal authenticator class: %s' % className)
                principalAuthenticator = self.createObjectInstance(moduleName, className, constructor)
                self.addAuthorizationPrincipalAuthenticator(principalAuthenticator)
                self.logger.debug('Authorization principal authenticator: %s' % (principalAuthenticator))

    def addAuthorizationPrincipalAuthenticator(self, principalAuthenticator):
        self.principalAuthenticatorList.append(principalAuthenticator)

    def getAuthorizationPrincipal(self, username, password):
        """ Get principal based on a username and password """
        # First try cache.
        #self.logger.debug('Trying username %s from the cache' % username)
        principal = None
        principalTuple = self.objectCache.get(username)
        if principalTuple is not None:
            (id, principal, updateTime, expirationTime) = principalTuple
        if principal is None:
            # Try principal retriever
            principal = self.principalRetriever.getAuthorizationPrincipal(username)

        if principal is None:
            self.logger.debug('No principal for username: %s' % username)
            return 
        
        # Try all authorization principal authenticators.
        for principalAuthenticator in self.principalAuthenticatorList:
            self.logger.debug('Attempting to authenticate %s by %s' % (username, principalAuthenticator.getName()))
            authenticatedPrincipal = principalAuthenticator.authenticatePrincipal(principal, password)
            if authenticatedPrincipal is not None:
                self.logger.debug('Adding authorization principal %s to the cache, authenticated by %s' % (principal.getName(),principalAuthenticator.getName()))
                self.objectCache.put(username, authenticatedPrincipal)
                return authenticatedPrincipal
        return None

    def removeAuthorizationPrincipal(self, username):
        """ Clear principal from the cache. """
        self.objectCache.remove(username)

#######################################################################
# Testing.
if __name__ == '__main__':
    am = AuthorizationManager.getInstance()
    authPrincipal = am.getAuthorizationPrincipal('sveseli', 'sv')
    print 'Auth principal: ', authPrincipal


