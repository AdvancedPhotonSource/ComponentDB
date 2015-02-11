#!/usr/bin/env python

import base64

class Encoder:

    @classmethod
    def encode(cls, data):
        # Encode twice, in order to avoid issues like '+' being
        # interpreted as space after decoding
        encodedData = base64.b64encode(base64.encodestring('%s' % data))
        return encodedData

    @classmethod
    def decode(cls, encodedData):
        data = base64.decodestring(base64.b64decode('%s' % encodedData))
        return data
