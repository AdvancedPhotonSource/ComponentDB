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
public class AuthenticationError extends CdbException 
{

    /**
     * Constructor.
     */
    public AuthenticationError() 
    {
        super();
        setErrorCode(CdbStatus.CDB_AUTHENTICATION_ERROR);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     */
    public AuthenticationError(String message) 
    {
        super(message);
        setErrorCode(CdbStatus.CDB_AUTHENTICATION_ERROR);
    }

    /**
     * Constructor.
     *
     * @param throwable Throwable object
     */
    public AuthenticationError(Throwable throwable) 
    {
        super(throwable);
        setErrorCode(CdbStatus.CDB_AUTHENTICATION_ERROR);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     * @param throwable Throwable object
     */
    public AuthenticationError(String message, Throwable throwable) 
    {
        super(message, throwable);
        setErrorCode(CdbStatus.CDB_AUTHENTICATION_ERROR);
    }

}