#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.objects.cdbObject import CdbObject


class PdmLinkDrawingRevision(CdbObject):

    DEFAULT_KEY_LIST = ['state', 'ufid', 'iteration', 'version', 'icmsUrl', 'dateModified']

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

