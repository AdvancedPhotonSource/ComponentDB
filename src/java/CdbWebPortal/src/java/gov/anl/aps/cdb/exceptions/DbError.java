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
public class DbError extends CdbException 
{

    /**
     * Constructor.
     */
    public DbError() 
    {
        super();
        setErrorCode(CdbStatus.CDB_DB_ERROR);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     */
    public DbError(String message) 
    {
        super(message);
        setErrorCode(CdbStatus.CDB_DB_ERROR);
    }

    /**
     * Constructor.
     *
     * @param throwable Throwable object
     */
    public DbError(Throwable throwable) 
    {
        super(throwable);
        setErrorCode(CdbStatus.CDB_DB_ERROR);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     * @param throwable Throwable object
     */
    public DbError(String message, Throwable throwable) 
    {
        super(message, throwable);
        setErrorCode(CdbStatus.CDB_DB_ERROR);
    }

}