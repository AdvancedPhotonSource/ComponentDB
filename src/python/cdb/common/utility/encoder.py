#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import base64

class Encoder:

    @classmethod
    def encode(cls, data):
        # Encode twice, in order to avoid issues like '+' being
        # interpreted as space after decoding
        if data is None:
            return None
        encodedData = base64.b64encode(base64.encodestring('%s' % data))
        return encodedData

    @classmethod
    def decode(cls, encodedData):
        if encodedData is None:
            return None
        data = base64.decodestring(base64.b64decode('%s' % encodedData))
        return data
