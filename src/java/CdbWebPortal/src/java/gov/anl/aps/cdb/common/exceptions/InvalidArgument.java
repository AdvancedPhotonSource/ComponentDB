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
 * Invalid argument exception.
 */
public class InvalidArgument extends CdbException {

    /**
     * Default constructor.
     */
    public InvalidArgument() {
        super();
        setErrorCode(CdbStatus.CDB_INVALID_ARGUMENT);
    }

    /**
     * Constructor using error message.
     *
     * @param message error message
     */
    public InvalidArgument(String message) {
        super(message);
        setErrorCode(CdbStatus.CDB_INVALID_ARGUMENT);
    }

    /**
     * Constructor using throwable object.
     *
     * @param throwable throwable object
     */
    public InvalidArgument(Throwable throwable) {
        super(throwable);
        setErrorCode(CdbStatus.CDB_INVALID_ARGUMENT);
    }

    /**
     * Constructor using error message and throwable object.
     *
     * @param message error message
     * @param throwable throwable object
     */
    public InvalidArgument(String message, Throwable throwable) {
        super(message, throwable);
        setErrorCode(CdbStatus.CDB_INVALID_ARGUMENT);
    }

}
