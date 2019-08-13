#!/usr/bin/env python

from cdbObject import CdbObject


class Image(CdbObject):

    DEFAULT_KEY_LIST = ['imageUrl', 'thumbnailUrl']

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

