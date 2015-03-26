/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.common.exceptions;


import gov.anl.aps.cdb.common.constants.CdbStatus;

/**
 * Object already exists exception class.
 */
public class InternalError extends CdbException 
{

    /**
     * Constructor.
     */
    public InternalError() 
    {
        super();
        setErrorCode(CdbStatus.CDB_INTERNAL_ERROR);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     */
    public InternalError(String message) 
    {
        super(message);
        setErrorCode(CdbStatus.CDB_INTERNAL_ERROR);
    }

    /**
     * Constructor.
     *
     * @param throwable Throwable object
     */
    public InternalError(Throwable throwable) 
    {
        super(throwable);
        setErrorCode(CdbStatus.CDB_INTERNAL_ERROR);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     * @param throwable Throwable object
     */
    public InternalError(String message, Throwable throwable) 
    {
        super(message, throwable);
        setErrorCode(CdbStatus.CDB_INTERNAL_ERROR);
    }

}