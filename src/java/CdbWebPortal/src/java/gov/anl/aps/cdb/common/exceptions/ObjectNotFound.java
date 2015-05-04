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
 * Object not found exception.
 */
public class ObjectNotFound extends CdbException {

    /**
     * Default constructor.
     */
    public ObjectNotFound() {
        super();
    }

    /**
     * Constructor using error message.
     *
     * @param message error message
     */
    public ObjectNotFound(String message) {
        super(message);
    }

    /**
     * Constructor using throwable object.
     *
     * @param throwable throwable object
     */
    public ObjectNotFound(Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructor using error message and throwable object.
     *
     * @param message error message
     * @param throwable throwable object
     */
    public ObjectNotFound(String message, Throwable throwable) {
        super(message, throwable);
    }

    @Override
    public int getErrorCode() {
        return CdbStatus.CDB_OBJECT_NOT_FOUND;
    }    
}
