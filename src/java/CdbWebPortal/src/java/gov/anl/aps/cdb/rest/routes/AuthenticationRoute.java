/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.portal.controllers.LoginController;
import gov.anl.aps.cdb.portal.model.db.beans.UserInfoFacade;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.rest.authentication.Secured;
import gov.anl.aps.cdb.rest.authentication.UserSessionKeeper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/login")
@Tag(name = "Authentication")
@SecurityScheme(name = "cdbAuth",
                type = SecuritySchemeType.APIKEY,
                in = SecuritySchemeIn.COOKIE,
                paramName = UserSessionKeeper.AUTH_TOKEN_KEY)
public class AuthenticationRoute extends BaseRoute {
    
    @EJB
    private UserInfoFacade userFacade;
            
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)    
    @Operation(summary = "Logs user into the system",
          responses = {
                  @ApiResponse(
                          responseCode = "200", 
                          description = "Successfully logged in",
                          headers = @Header(name = "token", 
                                  schema = @Schema(type = "string", 
                                          example = "tokenText"
                                  )
                          )
                  ),
                  @ApiResponse(responseCode = "400", 
                          description = "Invalid username/password supplied")
          }
    )
    public Response authenticateUser(@FormParam("username") String username, 
                                     @FormParam("password") String password) {
                      
        UserInfo userInfo = userFacade.findByUsername(username); 
        boolean authenticated = LoginController.validateCredentials(userInfo, password);
        
        if (authenticated) {                       
            UserSessionKeeper usk = UserSessionKeeper.getInstance(); 
            String token = usk.getToken(userInfo); 
                                    
            return Response.ok(usk.getNext()).header(UserSessionKeeper.AUTH_TOKEN_KEY, token).build(); 
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build(); 
        }
    }
    
    @GET
    @Path("/Verify")
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public boolean verifyAuthenticated() {
        return true; 
    }
    
}
