/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.common.exceptions;

import gov.anl.aps.cdb.common.constants.CdbStatus;

/**
 * Communication error exception.
 */
public class CommunicationError extends CdbException {

    /**
     * Default constructor.
     */
    public CommunicationError() {
        super();
    }

    /**
     * Constructor using error message.
     *
     * @param message error message
     */
    public CommunicationError(String message) {
        super(message);
    }

    /**
     * Constructor using throwable object.
     *
     * @param throwable throwable object
     */
    public CommunicationError(Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructor using error message and throwable object.
     *
     * @param message error message
     * @param throwable throwable object
     */
    public CommunicationError(String message, Throwable throwable) {
        super(message, throwable);
    }


    @Override
    public int getErrorCode() {
        return CdbStatus.CDB_COMMUNICATION_ERROR;
    }     
}
