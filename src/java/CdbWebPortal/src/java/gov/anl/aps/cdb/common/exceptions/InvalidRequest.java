/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: $
 *   $Date: $
 *   $Revision: $
 *   $Author: $
 */
package gov.anl.aps.cdb.common.exceptions;

import gov.anl.aps.cdb.common.constants.CdbStatus;

/**
 * Invalid request exception.
 */
public class InvalidRequest extends CdbException {

    /**
     * Default constructor.
     */
    public InvalidRequest() {
        super();
        setErrorCode(CdbStatus.CDB_INVALID_REQUEST);
    }

    /**
     * Constructor using error message.
     *
     * @param message error message
     */
    public InvalidRequest(String message) {
        super(message);
        setErrorCode(CdbStatus.CDB_INVALID_REQUEST);
    }

    /**
     * Constructor using throwable object.
     *
     * @param throwable throwable object
     */
    public InvalidRequest(Throwable throwable) {
        super(throwable);
        setErrorCode(CdbStatus.CDB_INVALID_REQUEST);
    }

    /**
     * Constructor using error message and throwable object.
     *
     * @param message error message
     * @param throwable throwable object
     */
    public InvalidRequest(String message, Throwable throwable) {
        super(message, throwable);
        setErrorCode(CdbStatus.CDB_INVALID_REQUEST);
    }

}
