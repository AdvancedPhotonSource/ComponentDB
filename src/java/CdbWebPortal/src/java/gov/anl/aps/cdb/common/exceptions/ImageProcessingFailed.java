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
 * Image processing failed exception.
 */
public class ImageProcessingFailed extends CdbException {

    /**
     * Default constructor.
     */
    public ImageProcessingFailed() {
        super();
    }

    /**
     * Constructor using error message.
     *
     * @param message error message
     */
    public ImageProcessingFailed(String message) {
        super(message);
    }

    /**
     * Constructor using throwable object.
     *
     * @param throwable throwable object
     */
    public ImageProcessingFailed(Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructor using error message and throwable object.
     *
     * @param message error message
     * @param throwable throwable object
     */
    public ImageProcessingFailed(String message, Throwable throwable) {
        super(message, throwable);
    }

    @Override
    public int getErrorCode() {
        return CdbStatus.CDB_IMAGE_PROCESSING_FAILED;
    }    
}
