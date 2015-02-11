/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.exceptions;


import gov.anl.aps.cdb.constants.CdbStatus;

/**
 * Object already exists exception class.
 */
public class InvalidArgument extends CdbException 
{

    /**
     * Constructor.
     */
    public InvalidArgument() 
    {
        super();
        setErrorCode(CdbStatus.CDB_INVALID_ARGUMENT);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     */
    public InvalidArgument(String message) 
    {
        super(message);
        setErrorCode(CdbStatus.CDB_INVALID_ARGUMENT);
    }

    /**
     * Constructor.
     *
     * @param throwable Throwable object
     */
    public InvalidArgument(Throwable throwable) 
    {
        super(throwable);
        setErrorCode(CdbStatus.CDB_INVALID_ARGUMENT);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     * @param throwable Throwable object
     */
    public InvalidArgument(String message, Throwable throwable) 
    {
        super(message, throwable);
        setErrorCode(CdbStatus.CDB_INVALID_ARGUMENT);
    }

}