#!/usr/bin/env python

#######################################################################

import cherrypy

from cdb.common.service.cdbController import CdbController
from cdb.common.objects.cdbObject import CdbObject
from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.internalError import InternalError
from cdb.common.exceptions.invalidRequest import InvalidRequest

from cdb.cdb_web_service.impl.fileSystemControllerImpl import FileSystemControllerImpl

#######################################################################

class FileSystemController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.fileSystemControllerImpl = FileSystemControllerImpl()


    @cherrypy.expose
    def getDirectoryList(self, directoryName, **kwargs):
        try:
            if not len(directoryName):
                raise InvalidRequest('Invalid directory name.')
            if not kwargs.has_key('parentDirectory'):
                raise InvalidRequest('Missing parent directory.')
            parentDirectory = kwargs.get('parentDirectory')
            path = '%s/%s' % (parentDirectory, directoryName)
            response = '%s' % self.fileSystemControllerImpl.getDirectoryList(path).getJsonRep()
            self.logger.debug('Returning path for %s: %s' % (path,response))
        except CdbException, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = ex.getJsonRep()
        except Exception, ex:
            self.logger.error('%s' % ex)
            self.handleException(ex)
            response = InternalError(ex).getJsonRep()
        return self.formatJsonResponse(response)

