/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.common.exceptions;

import gov.anl.aps.cdb.common.constants.CdbStatus;

/**
 * Configuration error exception.
 */
public class ConfigurationError extends CdbException {

    /**
     * Default constructor.
     */
    public ConfigurationError() {
        super();
    }

    /**
     * Constructor using error message.
     *
     * @param message error message
     */
    public ConfigurationError(String message) {
        super(message);
    }

    /**
     * Constructor sing throwable object.
     *
     * @param throwable throwable object
     */
    public ConfigurationError(Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructor using error message and throwable object.
     *
     * @param message error message
     * @param throwable throwable object
     */
    public ConfigurationError(String message, Throwable throwable) {
        super(message, throwable);
    }

    @Override
    public int getErrorCode() {
        return CdbStatus.CDB_CONFIGURATION_ERROR;
    }    
}
