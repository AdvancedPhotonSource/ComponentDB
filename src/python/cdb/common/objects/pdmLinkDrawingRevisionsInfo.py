#!/usr/bin/env python

from cdbObject import CdbObject


class PDMLinkDrawingRevisionsInfo(CdbObject):

    DEFAULT_KEY_LIST = ['name', 'windchillUrl', 'revisions', 'state', 'version', 'iteration', 'icmsUrl']

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

