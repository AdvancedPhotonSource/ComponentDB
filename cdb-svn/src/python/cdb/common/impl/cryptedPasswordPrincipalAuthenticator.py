#!/usr/bin/env python

from cdb.common.utility.cryptUtility import CryptUtility
from authorizationPrincipalAuthenticator import AuthorizationPrincipalAuthenticator 

class CryptedPasswordPrincipalAuthenticator(AuthorizationPrincipalAuthenticator):

    def __init__(self):
        AuthorizationPrincipalAuthenticator.__init__(self, self.__class__.__name__)

    def authenticatePrincipal(self, principal, password):
        if principal is not None:
            principalToken = principal.getToken()
            if principalToken is not None and len(principalToken):
                if CryptUtility.verifyPasswordWithPbkdf2(password, principalToken):
                    self.logger.debug('Authentication successful for %s' % principal.getName())
                    return principal
                else:
                    self.logger.debug('Authentication failed for %s' % principal.getName())
            else:
                self.logger.debug('Token is empty for %s, authentication not performed' % principal.getName())
        return None

#######################################################################
# Testing.
if __name__ == '__main__':
    pass

