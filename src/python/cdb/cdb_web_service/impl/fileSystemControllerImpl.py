#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Implementation for file system controller.
#

#######################################################################

import threading

from cdb.common.objects.cdbObject import CdbObject
from cdb.common.objects.cdbObjectManager import CdbObjectManager
from cdb.common.utility.cdbSubprocess import CdbSubprocess

#######################################################################

class FileSystemControllerImpl(CdbObjectManager):
    """ FS controller implementation class. """

    def __init__(self):
        CdbObjectManager.__init__(self)

    def getDirectoryList(self, path):
        p = CdbSubprocess('ls -l %s' % path)
        p.run()
        return CdbObject({'path' : path, 'directoryList' : p.getStdOut()})

    def writeFile(self, path, content):
        f = open(path, 'w')
        f.write('%s\n' % content)
        f.close()
        return CdbObject({'path' : path})
