#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from unittest import TestCase

from cdb.common.utility.ldapUtility import LdapUtility


class TestLdapUtility(TestCase):
    def test_checkCredentials(self):
        ldapUtility = LdapUtility(serverUrl='ldap://ldap.forumsys.com', dnFormat='uid=%s,dc=example,dc=com')
        try:
            ldapUtility.checkCredentials('einstein', 'password')
        except Exception as ex:
            self.fail("Test failed: " + ex.message)

        try:
            ldapUtility.checkCredentials('nonrealuser', 'fakepassword')
            self.fail("Exception should have been thrown. invalid credentials given")
        except Exception as ex:
            pass



