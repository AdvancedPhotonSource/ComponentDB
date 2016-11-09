#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#######################################################################

import cherrypy

from cdb.common.service.cdbController import CdbController
from cdb.common.exceptions.invalidRequest import InvalidRequest

from cdb.cdb_web_service.impl.fileSystemControllerImpl import FileSystemControllerImpl

#######################################################################

class FileSystemController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.fileSystemControllerImpl = FileSystemControllerImpl()

    @cherrypy.expose
    @CdbController.execute
    def getDirectoryList(self, directoryName, **kwargs):
        if not len(directoryName):
            raise InvalidRequest('Invalid directory name.')
        if not kwargs.has_key('parentDirectory'):
            raise InvalidRequest('Missing parent directory.')
        parentDirectory = kwargs.get('parentDirectory')
        path = '%s/%s' % (parentDirectory, directoryName)
        response = self.fileSystemControllerImpl.getDirectoryList(path).getFullJsonRep()
        self.logger.debug('Returning path for %s: %s' % (path,response))
        return response

