/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

/**
 *
 * @author djarosz
 */
public class ApiExceptionMessage {
    
    private String simpleName;      
    private String message; 
    private Exception exception; 

    public ApiExceptionMessage(Exception exception) {
        simpleName = exception.getClass().getSimpleName(); 
        message = exception.getMessage();         
        this.exception = exception;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
    
}
