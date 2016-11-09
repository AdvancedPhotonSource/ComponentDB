#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import ssl

class SslUtility:

    DEFAULT_SSL_CONTEXT = ssl._create_default_https_context

    @classmethod
    def useUnverifiedSslContext(cls, func):

        def wrapper(*args, **kwargs):
            # Disable SSL checking
            ssl._create_default_https_context = ssl._create_unverified_context

            # Perform function call 
            result = func(*args, **kwargs)

            # Revert back to original SSL settings
            ssl._create_default_https_context = SslUtility.DEFAULT_SSL_CONTEXT 
            return result

        return wrapper

