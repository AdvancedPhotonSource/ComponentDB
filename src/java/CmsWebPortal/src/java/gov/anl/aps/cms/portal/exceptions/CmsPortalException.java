/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.exceptions;

import gov.anl.aps.cms.portal.constants.CmsStatus;


/**
 * Generic DAQ exception.
 */
public class CmsPortalException extends Exception
{

    /**
     * Exception signature key.
     */
    public static final String SignatureKey = "__cms_portal_exception__";

    /**
     * Exception type key.
     */
    public static final String TypeKey = "type";

    /**
     * Exception code key.
     */
    public static final String CodeKey = "code";

    /**
     * Exception args key.
     */
    public static final String ArgsKey = "args";

    /**
     * Error code.
     */
    private int errorCode = CmsStatus.CMS_ERROR;


    /**
     * Error message. 
     */
    private String error = null;

    /**
     * Constructor.
     */
    public CmsPortalException() 
    {
        super();
    }

    /**
     * Constructor.
     *
     * @param message Error message
     */
    public CmsPortalException(String message) 
    {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     * @param errorCode Error code
     */
    public CmsPortalException(String message, int errorCode) 
    {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructor.
     *
     * @param throwable Throwable object
     */
    public CmsPortalException(Throwable throwable) 
    {
        super(throwable);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     * @param throwable Throwable object
     */
    public CmsPortalException(String message, Throwable throwable) 
    {
        super(message, throwable);
    }

    /**
     * Set error code.
     * 
     * @param errorCode Error code
     */
    public void setErrorCode(int errorCode) 
    {
        this.errorCode = errorCode;
    }

    /**
     * Get error code.
     * 
     * @return Error code
     */
    public int getErrorCode() 
    {
        return errorCode;
    }

    /**
     * Set error message.
     * 
     * @param error Error message
     */
    public void setErrorMessage(String error) 
    {
        this.error = error;
    }

    /**
     * Get error message.
     * 
     * @return Error message
     */
    public String getErrorMessage() 
    {
        return error;
    }

    /**
     * Override string output if error message is set.
     * 
     * @return error message
     */
    @Override
    public String toString() 
    {
        if(error != null) {
            return error;
        }
        else {
            return super.toString();
        }
    }
}
