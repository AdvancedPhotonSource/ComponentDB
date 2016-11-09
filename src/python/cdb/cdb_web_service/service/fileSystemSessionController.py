#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#######################################################################

import cherrypy

from cdb.common.service.cdbSessionController import CdbSessionController
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.utility.encoder import Encoder

from cdb.cdb_web_service.impl.fileSystemControllerImpl import FileSystemControllerImpl


#######################################################################

class FileSystemSessionController(CdbSessionController):

    def __init__(self):
        CdbSessionController.__init__(self)
        self.fileSystemControllerImpl = FileSystemControllerImpl()

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def writeFile(self, fileName, **kwargs):
        if not kwargs.has_key('parentDirectory'):
            raise InvalidRequest('Missing parent directory.')
        parentDirectory = kwargs.get('parentDirectory')
        encodedFileContent = kwargs.get('encodedFileContent', '')
        fileContent = Encoder.decode(encodedFileContent) 
        filePath = '%s/%s' % (parentDirectory, fileName)
        response = '%s' % self.fileSystemControllerImpl.writeFile(filePath, fileContent).getFullJsonRep()
        self.logger.debug('Returning: %s' % response)
        return response

