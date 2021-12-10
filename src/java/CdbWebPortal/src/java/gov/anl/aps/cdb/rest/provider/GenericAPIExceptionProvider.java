/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.provider;

import gov.anl.aps.cdb.common.exceptions.AuthenticationError;
import gov.anl.aps.cdb.common.exceptions.ObjectNotFound;
import gov.anl.aps.cdb.rest.entities.ApiExceptionMessage;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Provider
public class GenericAPIExceptionProvider extends Exception implements ExceptionMapper<Exception> {

    private static final Logger logger = LogManager.getLogger(GenericAPIExceptionProvider.class.getName());
    
    @Override
    public Response toResponse(Exception exception) {
        int statusCode = 500; 
        
        if (exception instanceof NotAuthorizedException || exception instanceof AuthenticationError) {
            statusCode = 401;
        }
        if (exception instanceof ObjectNotFound) {
            statusCode = 404; 
        }
        
        logger.error(exception); 
        ApiExceptionMessage message = new ApiExceptionMessage(exception);         
        return Response.status(statusCode).entity(message).type(MediaType.APPLICATION_JSON).build();
    }
    
}
