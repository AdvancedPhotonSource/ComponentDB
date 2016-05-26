/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 */
package gov.anl.aps.cdb.common.exceptions;

import gov.anl.aps.cdb.common.constants.CdbStatus;

/**
 * External Service Error exception.
 */
public class ExternalServiceError extends CdbException {

    /**
     * Default constructor.
     */
    public ExternalServiceError() {
        super();
    }

    /**
     * Constructor using error message.
     *
     * @param message error message
     */
    public ExternalServiceError(String message) {
        super(message);
    }

    /**
     * Constructor using throwable object.
     *
     * @param throwable throwable object
     */
    public ExternalServiceError(Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructor using error message and throwable object.
     *
     * @param message error message
     * @param throwable throwable object
     */
    public ExternalServiceError(String message, Throwable throwable) {
        super(message, throwable);
    }

    @Override
    public int getErrorCode() {
        return CdbStatus.CDB_EXTERNAL_SERVICE_ERROR;
    }    
}
