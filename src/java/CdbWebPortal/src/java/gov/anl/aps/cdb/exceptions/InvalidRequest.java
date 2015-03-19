
package gov.anl.aps.cdb.exceptions;


import gov.anl.aps.cdb.constants.CdbStatus;

/**
 * Object already exists exception class.
 */
public class InvalidRequest extends CdbException 
{

    /**
     * Constructor.
     */
    public InvalidRequest() 
    {
        super();
        setErrorCode(CdbStatus.CDB_INVALID_REQUEST);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     */
    public InvalidRequest(String message) 
    {
        super(message);
        setErrorCode(CdbStatus.CDB_INVALID_REQUEST);
    }

    /**
     * Constructor.
     *
     * @param throwable Throwable object
     */
    public InvalidRequest(Throwable throwable) 
    {
        super(throwable);
        setErrorCode(CdbStatus.CDB_INVALID_REQUEST);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     * @param throwable Throwable object
     */
    public InvalidRequest(String message, Throwable throwable) 
    {
        super(message, throwable);
        setErrorCode(CdbStatus.CDB_INVALID_REQUEST);
    }

}