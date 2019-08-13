#!/usr/bin/env python

from cdb.common.constants import cdbRole
from cdb.common.objects.authorizationPrincipal import AuthorizationPrincipal
from cdb.common.utility.cryptUtility import CryptUtility
from authorizationPrincipalRetriever import AuthorizationPrincipalRetriever 

class NoOpPrincipalRetriever(AuthorizationPrincipalRetriever):

    def __init__(self):
        AuthorizationPrincipalRetriever.__init__(self, self.__class__.__name__)

    def getAuthorizationPrincipal(self, username):
        noOpPassword = CryptUtility.cryptPasswordWithPbkdf2(username)
        principal = AuthorizationPrincipal(username, noOpPassword)
        principal.setRole(cdbRole.CDB_USER_ROLE)
        if self.adminGroupName is not None:
            principal.setRole(cdbRole.CDB_ADMIN_ROLE)
        return principal

#######################################################################
# Testing.
if __name__ == '__main__':
    pass

