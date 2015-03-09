#!/usr/bin/env python

from cdb.common.utility.ldapUtility import LdapUtility
from authorizationPrincipalAuthenticator import AuthorizationPrincipalAuthenticator 

class LdapPasswordPrincipalAuthenticator(AuthorizationPrincipalAuthenticator):

    def __init__(self, serverUrl, dnFormat):
        AuthorizationPrincipalAuthenticator.__init__(self, self.__class__.__name__)
        self.ldapUtility = LdapUtility(serverUrl, dnFormat)

    def authenticatePrincipal(self, principal, password):
        if principal is not None:
            try:
                self.logger.debug('Checking credentials for %s' % principal.getName())
                self.ldapUtility.checkCredentials(principal.getName(), password)
                return principal
            except Exception, ex:
                self.logger.debug(ex)
        return None

#######################################################################
# Testing.
if __name__ == '__main__':
    pass

