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
public class ObjectNotFound extends CdbException 
{

    /**
     * Constructor.
     */
    public ObjectNotFound() 
    {
        super();
        setErrorCode(CdbStatus.CDB_OBJECT_NOT_FOUND);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     */
    public ObjectNotFound(String message) 
    {
        super(message);
        setErrorCode(CdbStatus.CDB_OBJECT_NOT_FOUND);
    }

    /**
     * Constructor.
     *
     * @param throwable Throwable object
     */
    public ObjectNotFound(Throwable throwable) 
    {
        super(throwable);
        setErrorCode(CdbStatus.CDB_OBJECT_NOT_FOUND);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     * @param throwable Throwable object
     */
    public ObjectNotFound(String message, Throwable throwable) 
    {
        super(message, throwable);
        setErrorCode(CdbStatus.CDB_OBJECT_NOT_FOUND);
    }

}