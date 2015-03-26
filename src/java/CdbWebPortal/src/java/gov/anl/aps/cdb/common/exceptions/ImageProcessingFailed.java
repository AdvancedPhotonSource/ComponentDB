/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.common.exceptions;


import gov.anl.aps.cdb.common.constants.CdbStatus;

/**
 * Image processing failed exception class.
 */
public class ImageProcessingFailed extends CdbException 
{

    /**
     * Constructor.
     */
    public ImageProcessingFailed() 
    {
        super();
        setErrorCode(CdbStatus.CDB_IMAGE_PROCESSING_FAILED);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     */
    public ImageProcessingFailed(String message) 
    {
        super(message);
        setErrorCode(CdbStatus.CDB_IMAGE_PROCESSING_FAILED);
    }

    /**
     * Constructor.
     *
     * @param throwable Throwable object
     */
    public ImageProcessingFailed(Throwable throwable) 
    {
        super(throwable);
        setErrorCode(CdbStatus.CDB_IMAGE_PROCESSING_FAILED);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     * @param throwable Throwable object
     */
    public ImageProcessingFailed(String message, Throwable throwable) 
    {
        super(message, throwable);
        setErrorCode(CdbStatus.CDB_IMAGE_PROCESSING_FAILED);
    }

}