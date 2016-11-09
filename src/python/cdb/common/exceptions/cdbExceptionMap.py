#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.constants import cdbStatus

CDB_EXCEPTION_MAP = {
    cdbStatus.CDB_ERROR : 'cdbException.CdbException',
    cdbStatus.CDB_INTERNAL_ERROR : 'internalError.InternalError',
    cdbStatus.CDB_COMMUNICATION_ERROR : 'communicationError.CommunicationError',
    cdbStatus.CDB_CONFIGURATION_ERROR : 'configurationError.ConfigurationError',
    cdbStatus.CDB_AUTHORIZATION_ERROR : 'authorizationError.AuthorizationError',
    cdbStatus.CDB_AUTHENTICATION_ERROR : 'authenticationError.AuthenticationError',
    cdbStatus.CDB_DB_ERROR : 'dbError.DbError',
    cdbStatus.CDB_URL_ERROR : 'urlError.UrlError',
    cdbStatus.CDB_INVALID_ARGUMENT: 'invalidArgument.InvalidArgument',
    cdbStatus.CDB_INVALID_REQUEST: 'invalidRequest.InvalidRequest',
    cdbStatus.CDB_INVALID_SESSION: 'invalidSession.InvalidSession',
    cdbStatus.CDB_COMMAND_FAILED: 'commandFailed.CommandFailed',
    cdbStatus.CDB_OBJECT_NOT_FOUND : 'objectNotFound.ObjectNotFound',
    cdbStatus.CDB_OBJECT_ALREADY_EXISTS: 'objectAlreadyExists.ObjectAlreadyExists',
    cdbStatus.CDB_INVALID_OBJECT_STATE: 'invalidObjectState.InvalidObjectState',
    cdbStatus.CDB_IMAGE_PROCESSING_FAILED : 'imageProcessingFailed.ImageProcessingFailed',
    cdbStatus.CDB_MULTIPLE_OBJECTS_FOUND : 'multipleObjectsFound.MultipleObjectsFound',
}

