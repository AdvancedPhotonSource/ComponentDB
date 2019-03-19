/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.portal.controllers.LoginController;
import gov.anl.aps.cdb.portal.model.db.beans.UserInfoFacade;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.rest.authentication.UserSessionKeeper;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

@Path("/login")
public class AuthenticationRoute {
    
    @EJB
    private UserInfoFacade userFacade;
            
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@FormParam("username") String username, 
                                     @FormParam("password") String password) {
                      
        UserInfo userInfo = userFacade.findByUsername(username); 
        boolean authenticated = LoginController.validateCredentials(userInfo, password);
        
        if (authenticated) {                       
            UserSessionKeeper usk = UserSessionKeeper.getInstance(); 
            NewCookie[] token = usk.getToken(userInfo); 
                                    
            return Response.ok(usk.getNext()).cookie(token).build(); 
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build(); 
        }
    }
    
}
