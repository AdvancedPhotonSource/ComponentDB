/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.AuthenticationError;
import gov.anl.aps.cdb.portal.controllers.LoginController;
import gov.anl.aps.cdb.portal.model.db.beans.UserInfoFacade;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.rest.authentication.Secured;
import gov.anl.aps.cdb.rest.authentication.User;
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
import java.security.Principal;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;

@Tag(name = "Authentication")
@Path("/auth")
@SecurityScheme(name = "cdbAuth",
                type = SecuritySchemeType.APIKEY,
                in = SecuritySchemeIn.COOKIE,
                paramName = UserSessionKeeper.AUTH_TOKEN_KEY)
public class AuthenticationRoute extends BaseRoute {
    
    private static final Logger LOGGER = Logger.getLogger(AuthenticationRoute.class.getName());
    
    @EJB
    private UserInfoFacade userFacade;
            
    @POST
    @Path("/login")
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
                                     @FormParam("password") String password) throws AuthenticationError {
        LOGGER.debug("Authenticating user: " + username);
        UserInfo userInfo = userFacade.findByUsername(username); 
        boolean authenticated = LoginController.validateCredentials(userInfo, password);
        
        if (authenticated) {                       
            UserSessionKeeper usk = UserSessionKeeper.getInstance(); 
            String token = usk.getToken(userInfo); 
                                    
            return Response.ok(usk.getNext()).header(UserSessionKeeper.AUTH_TOKEN_KEY, token).build(); 
        } else {
            throw new AuthenticationError("Could not verify username or password."); 
        }
    }
    
    @POST
    @Path("/logout")
    @Secured
    public void logOut() throws AuthenticationError, Exception {
        Principal userPrincipal = securityContext.getUserPrincipal();
        if (userPrincipal instanceof User) {
            String token = ((User) userPrincipal).getToken();
            LOGGER.debug("Logging out user: " + userPrincipal.getName());
            UserSessionKeeper sessionKeeper = UserSessionKeeper.getInstance();
            if (!sessionKeeper.revokeToken(token)) {
                throw new Exception("An error occurred logging user out."); 
            }
        } else {
            throw new AuthenticationError("Could not fetch current logged in user."); 
        }
    }
    
    @GET
    @Path("/Verify")
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public boolean verifyAuthenticated() {
        LOGGER.debug("User is authenticated.");
        return true; 
    }
    
}
