/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.portal.model.db.beans.UserInfoFacade;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Path("/Users")
@Tag(name = "Users")
public class UsersRoute extends BaseRoute {
    
    private static final Logger LOGGER = Logger.getLogger(UsersRoute.class.getName());
    
    @EJB
    UserInfoFacade userInfoFacade; 
    
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserInfo> getAll() { 
        LOGGER.debug("Fetching all users.");
        return userInfoFacade.findAll(); 
    }
    
    @GET
    @Path("/ByUsername/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserInfo getUserByUsername(@PathParam("username") String username) {
        LOGGER.debug("Fetching user by username: " + username); 
        return userInfoFacade.findByUsername(username);
    }        
    
}
