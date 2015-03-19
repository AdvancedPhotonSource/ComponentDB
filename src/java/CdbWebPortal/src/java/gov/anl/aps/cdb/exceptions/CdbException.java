
package gov.anl.aps.cdb.exceptions;

import gov.anl.aps.cdb.constants.CdbStatus;


/**
 * Generic CDB exception.
 */
public class CdbException extends Exception
{

    /**
     * Exception keys.
     */
    public static final String SIGNATURE_KEY = "__cdb_portal_exception__";
    public static final String TYPE_KEY = "type";
    public static final String CODE_KEY = "code";
    public static final String ARGS_KEY = "args";


    private int errorCode = CdbStatus.CDB_ERROR;
    private String error = null;

    /**
     * Constructor.
     */
    public CdbException() 
    {
        super();
    }

    /**
     * Constructor.
     *
     * @param message Error message
     */
    public CdbException(String message) 
    {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     * @param errorCode Error code
     */
    public CdbException(String message, int errorCode) 
    {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructor.
     *
     * @param throwable Throwable object
     */
    public CdbException(Throwable throwable) 
    {
        super(throwable);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     * @param throwable Throwable object
     */
    public CdbException(String message, Throwable throwable) 
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
        if (error != null) {
            return error;
        }
        return super.getMessage();
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
