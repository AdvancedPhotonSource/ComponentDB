#!/usr/bin/env python

from cdb.common.constants import cdbRole
from cdb.common.objects.authorizationPrincipal import AuthorizationPrincipal
from cdb.common.utility.cryptUtility import CryptUtility

class NoOpAuthorizationPrincipalRetriever:

    def __init__(self):
        pass

    def getAuthorizationPrincipal(self, username):
        noOpPassword = CryptUtility.cryptPassword(username)
        principal = AuthorizationPrincipal(username, noOpPassword)
        principal.setRole(cdbRole.CDB_ADMIN_ROLE)
        return principal

#######################################################################
# Testing.
if __name__ == '__main__':
    pass

