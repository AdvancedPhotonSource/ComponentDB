#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import ldap

from cdb.common.exceptions.authenticationError import AuthenticationError
from cdb.common.exceptions.communicationError import CommunicationError

class LdapUtility:

    def __init__(self, serverUrl, dnFormat, serviceDn = None, servicePass = None, userLookupFilter= None):
        self.serverUrl = serverUrl
        self.dnFormat = dnFormat
        self.serviceDn = serviceDn
        self.servicePass = servicePass
        self.userLookupFilter = userLookupFilter
         
    def checkCredentials(self, username, password):
        """Verifies credentials for username and password. """
        ldapUsername = self.__generateUserDn(username)
        self.__bindUser(ldapUsername, password)

    def __bindUser(self, bindDn, bindPass):
        try:
            # build client
            ldap.set_option(ldap.OPT_X_TLS_REQUIRE_CERT, ldap.OPT_X_TLS_NEVER)
            ldapClient = ldap.initialize(self.serverUrl)
            ldapClient.set_option(ldap.OPT_REFERRALS, 0)
            ldapClient.set_option(ldap.OPT_PROTOCOL_VERSION, ldap.VERSION3)

            # ldapClient.ststart_tls_s()
            # ldapClient.set_option(ldap.OPT_X_TLS,ldap.OPT_X_TLS_DEMAND)
            # ldapClient.set_option( ldap.OPT_X_TLS_DEMAND, True)
            # ldapClient.set_option( ldap.OPT_DEBUG_LEVEL, 255)

            # perform a synchronous bind
            ldapClient.simple_bind_s(bindDn, bindPass)
            return ldapClient
            # ldapClient.whoami_s()
        except ldap.INVALID_CREDENTIALS, ex:
            ldapClient.unbind()
            raise AuthenticationError('Invalid LDAP credentials for user %s' % bindDn)
        except ldap.SERVER_DOWN, ex:
            raise CommunicationError('Cannot reach LDAP server %s' % self.serverUrl)

    def __generateUserDn(self, username):
        if self.serviceDn is None:
            return self.dnFormat % username
        else:
            ldapClient = self.__bindUser(self.serviceDn, self.servicePass)
            filter = self.userLookupFilter % username

            resId = ldapClient.search(self.dnFormat, ldap.SCOPE_SUBTREE, filter)
            result_type, res = ldapClient.result(resId, 0)

            if res != []:
                return res[0][0]

            raise AuthenticationError("Cannot find user dn for user %s" % username)

#######################################################################
# Testing.

if __name__ == '__main__':
    ldapUtility = LdapUtility(serverUrl='ldap://ldap.forumsys.com', dnFormat='uid=%s,dc=example,dc=com')
    ldapUtility.checkCredentials('einstein', 'password')
