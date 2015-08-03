#!/usr/bin/env python

from cdbObject import CdbObject


class PdmLinkDrawingRevision(CdbObject):

    DEFAULT_KEY_LIST = ['state', 'ufid', 'iteration', 'version', 'icmsUrl', 'dateCreated']

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

