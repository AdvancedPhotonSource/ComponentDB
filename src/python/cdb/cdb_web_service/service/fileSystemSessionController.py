#!/usr/bin/env python

#######################################################################

import cherrypy

from cdb.common.service.cdbSessionController import CdbSessionController
from cdb.common.objects.cdbObject import CdbObject
from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.internalError import InternalError
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.service.loginController import LoginController
from cdb.common.utility.encoder import Encoder

from cdb.cdb_web_service.impl.fileSystemControllerImpl import FileSystemControllerImpl


#######################################################################

class FileSystemSessionController(CdbSessionController):

    def __init__(self):
        CdbSessionController.__init__(self)
        self.fileSystemControllerImpl = FileSystemControllerImpl()

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    def writeFile(self, fileName, **kwargs):
        try:
            if not kwargs.has_key('parentDirectory'):
                raise InvalidRequest('Missing parent directory.')
            parentDirectory = kwargs.get('parentDirectory')
            encodedFileContent = kwargs.get('encodedFileContent', '')
            fileContent = Encoder.decode(encodedFileContent) 
            filePath = '%s/%s' % (parentDirectory, fileName)
            response = '%s' % self.fileSystemControllerImpl.writeFile(filePath, fileContent).getJsonRep()
            self.logger.debug('Returning: %s' % response)
        except CdbException, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = ex.getJsonRep()
        except Exception, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = InternalError(ex).getJsonRep()
        return self.formatJsonResponse(response)

