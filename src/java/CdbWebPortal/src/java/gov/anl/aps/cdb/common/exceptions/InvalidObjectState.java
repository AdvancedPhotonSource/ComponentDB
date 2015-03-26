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
public class InvalidObjectState extends CdbException 
{

    /**
     * Constructor.
     */
    public InvalidObjectState() 
    {
        super();
        setErrorCode(CdbStatus.CDB_INVALID_OBJECT_STATE);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     */
    public InvalidObjectState(String message) 
    {
        super(message);
        setErrorCode(CdbStatus.CDB_INVALID_OBJECT_STATE);
    }

    /**
     * Constructor.
     *
     * @param throwable Throwable object
     */
    public InvalidObjectState(Throwable throwable) 
    {
        super(throwable);
        setErrorCode(CdbStatus.CDB_INVALID_OBJECT_STATE);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     * @param throwable Throwable object
     */
    public InvalidObjectState(String message, Throwable throwable) 
    {
        super(message, throwable);
        setErrorCode(CdbStatus.CDB_INVALID_OBJECT_STATE);
    }

}