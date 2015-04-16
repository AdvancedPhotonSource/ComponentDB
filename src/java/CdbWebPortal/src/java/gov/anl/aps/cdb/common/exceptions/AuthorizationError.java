/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.common.exceptions;

import gov.anl.aps.cdb.common.constants.CdbStatus;

/**
 * Authorization error exception.
 */
public class AuthorizationError extends CdbException {

    /**
     * Default constructor.
     */
    public AuthorizationError() {
        super();
        setErrorCode(CdbStatus.CDB_AUTHORIZATION_ERROR);
    }

    /**
     * Constructor using error message.
     *
     * @param message error message
     */
    public AuthorizationError(String message) {
        super(message);
        setErrorCode(CdbStatus.CDB_AUTHORIZATION_ERROR);
    }

    /**
     * Constructor using throwable object.
     *
     * @param throwable throwable object
     */
    public AuthorizationError(Throwable throwable) {
        super(throwable);
        setErrorCode(CdbStatus.CDB_AUTHORIZATION_ERROR);
    }

    /**
     * Constructor using error message and throwable object.
     *
     * @param message error message
     * @param throwable throwable object
     */
    public AuthorizationError(String message, Throwable throwable) {
        super(message, throwable);
        setErrorCode(CdbStatus.CDB_AUTHORIZATION_ERROR);
    }

}
