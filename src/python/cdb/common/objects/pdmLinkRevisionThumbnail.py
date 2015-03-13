#!/usr/bin/env python

from cdbObject import CdbObject


class PDMLinkRevisionThumbnail(CdbObject):

    DEFAULT_KEY_LIST = ['thumbnailUrl']

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

