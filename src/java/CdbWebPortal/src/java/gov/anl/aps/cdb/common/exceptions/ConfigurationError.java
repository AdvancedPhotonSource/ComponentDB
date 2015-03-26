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
public class ConfigurationError extends CdbException 
{

    /**
     * Constructor.
     */
    public ConfigurationError() 
    {
        super();
        setErrorCode(CdbStatus.CDB_CONFIGURATION_ERROR);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     */
    public ConfigurationError(String message) 
    {
        super(message);
        setErrorCode(CdbStatus.CDB_CONFIGURATION_ERROR);
    }

    /**
     * Constructor.
     *
     * @param throwable Throwable object
     */
    public ConfigurationError(Throwable throwable) 
    {
        super(throwable);
        setErrorCode(CdbStatus.CDB_CONFIGURATION_ERROR);
    }

    /**
     * Constructor.
     *
     * @param message Error message
     * @param throwable Throwable object
     */
    public ConfigurationError(String message, Throwable throwable) 
    {
        super(message, throwable);
        setErrorCode(CdbStatus.CDB_CONFIGURATION_ERROR);
    }

}