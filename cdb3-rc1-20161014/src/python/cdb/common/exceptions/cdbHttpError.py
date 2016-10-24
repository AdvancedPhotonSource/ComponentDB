#!/usr/bin/env python

import cherrypy
from cherrypy import HTTPError

class CdbHttpError(HTTPError):
    def __init__ (self, httpCode, httpError, cdbEx):
        HTTPError.__init__(self, httpCode, httpError)
        self.cdbException = cdbEx

    def set_response(self):
        HTTPError.set_response(self)
        cherrypy.response.headers['Cdb-Status-Code'] = self.cdbException.getErrorCode()
        cherrypy.response.headers['Cdb-Status-Message'] = self.cdbException.getErrorMessage()
        cherrypy.response.headers['Cdb-Exception-Type'] = self.cdbException.getExceptionType()
