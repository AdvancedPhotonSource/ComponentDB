#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Base CDB controller class.
#

#######################################################################

import cherrypy
import httplib
import json

from sys import exc_info
from cdb.common.utility.loggingManager import LoggingManager
from cdb.common.constants import cdbStatus
from cdb.common.constants import cdbHttpHeaders
from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions import cdbExceptionMap
from cdb.common.exceptions.internalError import InternalError

#######################################################################

class CdbController(object):
    """ Base controller class. """
    def __init__(self):
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)

    @classmethod
    def getLogger(cls):
        logger = LoggingManager.getInstance().getLogger(cls.__name__)
        return logger

    @classmethod
    def addCdbResponseHeaders(cls, status=cdbStatus.CDB_OK, msg='Success', exceptionType=None):
        cherrypy.response.headers[cdbHttpHeaders.CDB_STATUS_CODE_HTTP_HEADER] = status
        cherrypy.response.headers[cdbHttpHeaders.CDB_STATUS_MESSAGE_HTTP_HEADER] = msg
        if exceptionType is not None:
            cherrypy.response.headers[cdbHttpHeaders.CDB_EXCEPTION_TYPE_HTTP_HEADER] = exceptionType

    @classmethod
    def addCdbSessionRoleHeaders(cls, role):
        cherrypy.response.headers[cdbHttpHeaders.CDB_SESSION_ROLE_HTTP_HEADER] = role

    @classmethod
    def addCdbExceptionHeaders(cls, ex):
        cls.handleException(ex)

    @classmethod
    def handleCpException(cls):
        cherrypy.response.status = httplib.OK
        ex = exc_info()[1]
        if ex == None:
            ex = InternalError('Internal Webservice Error')
        cls.handleException(ex)

    @classmethod
    def handleException(cls, ex):
        exClass = ex.__class__.__name__.split('.')[-1]
        status = None
        msg = '%s' % ex
        msg = msg.split('\n')[0]
        for code in cdbExceptionMap.CDB_EXCEPTION_MAP.keys():
            exStr = cdbExceptionMap.CDB_EXCEPTION_MAP.get(code).split('.')[-1]
            if exStr == exClass:
                status = code
        if not status:
            status = cdbStatus.CDB_ERROR
        cls.addCdbResponseHeaders(status, msg, exClass)

    @classmethod
    def formatJsonResponse(cls, response):
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return '%s' % (response)

    @classmethod
    def toJson(cls, o):
        return json.dumps(o,  encoding='latin_1')

    @classmethod
    def fromJson(cls, s):
        return json.loads(s)

    @classmethod
    def listToJson(cls, cdbObjectList):
        jsonList = []
        for cdbObject in cdbObjectList:
            jsonList.append(cdbObject.getDictRep(keyList='__all__'))
        jsonList = str(jsonList)
        return json.dumps(jsonList, encoding='latin_1')

    @classmethod
    def getSessionUser(cls):
        return cherrypy.serving.session.get('user')

    @classmethod
    def getSessionUsername(cls):
        return cherrypy.serving.session.get('_cp_username')

    # Exception decorator for all exposed method calls
    @classmethod
    def execute(cls, func):
        def decorate(*args, **kwargs):
            try:
                response = func(*args, **kwargs)
                cherrypy.response.headers["Access-Control-Allow-Origin"] = "*"
            except CdbException, ex:
                cls.getLogger().error('%s' % ex)
                cls.handleException(ex)
                response = ex.getFullJsonRep()
            except Exception, ex:
                cls.getLogger().error('%s' % ex)
                cls.handleException(ex)
                response = InternalError(ex).getFullJsonRep()
            return cls.formatJsonResponse(response)
        return decorate

