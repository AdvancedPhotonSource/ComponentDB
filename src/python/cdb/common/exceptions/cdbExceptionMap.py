#!/usr/bin/env python

from cdb.common.constants import cdbStatus

CDB_EXCEPTION_MAP = {
    cdbStatus.CDB_ERROR : 'cdbException.CdbException',
    cdbStatus.CDB_CONFIGURATION_ERROR : 'configurationError.ConfigurationError',
    cdbStatus.CDB_AUTHORIZATION_ERROR : 'authorizationError.AuthorizationError',
    cdbStatus.CDB_INTERNAL_ERROR : 'internalError.InternalError',
    cdbStatus.CDB_INVALID_ARGUMENT_ERROR : 'invalidArgument.InvalidArgument',
    cdbStatus.CDB_INVALID_REQUEST_ERROR : 'invalidRequest.InvalidRequest',
    cdbStatus.CDB_COMMAND_FAILED_ERROR : 'commandFailed.CommandFailed',
    cdbStatus.CDB_URL_ERROR : 'urlError.UrlError',
}
