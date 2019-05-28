/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.rest.authentication.Secured;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author djarosz
 */
@Path("/test")
@Tag(name = "Test")
public class TestRoute {
    
    @GET
    @Path("/Auth")
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public boolean verifyAuthenticated() {
        return true; 
    }
    
    @GET
    @Path("/NoAuth")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean verifyConnection() {
        return true; 
    }
    
}
