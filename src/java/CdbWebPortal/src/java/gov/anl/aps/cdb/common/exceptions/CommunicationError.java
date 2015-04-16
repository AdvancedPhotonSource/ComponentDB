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
 * Communication error exception.
 */
public class CommunicationError extends CdbException {

    /**
     * Default constructor.
     */
    public CommunicationError() {
        super();
        setErrorCode(CdbStatus.CDB_COMMUNICATION_ERROR);
    }

    /**
     * Constructor using error message.
     *
     * @param message error message
     */
    public CommunicationError(String message) {
        super(message);
        setErrorCode(CdbStatus.CDB_COMMUNICATION_ERROR);
    }

    /**
     * Constructor using throwable object.
     *
     * @param throwable throwable object
     */
    public CommunicationError(Throwable throwable) {
        super(throwable);
        setErrorCode(CdbStatus.CDB_COMMUNICATION_ERROR);
    }

    /**
     * Constructor using error message and throwable object.
     *
     * @param message error message
     * @param throwable throwable object
     */
    public CommunicationError(String message, Throwable throwable) {
        super(message, throwable);
        setErrorCode(CdbStatus.CDB_COMMUNICATION_ERROR);
    }

}
