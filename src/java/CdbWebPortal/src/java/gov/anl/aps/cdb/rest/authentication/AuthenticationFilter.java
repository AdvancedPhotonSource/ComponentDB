/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.authentication;

import java.io.IOException;
import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    
    /**
     * TODO: Remove this comment 
     * AUTHENTICATION STILL UNDER CONSTRUCTION.
     * USERFUL RESOURCE: https://github.com/Posya/wiki/blob/master/best-practice-for-rest-token-based-authentication-with-jax-rs-and-jersey.adoc     
     */

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Get the HTTP Authorization header from the request
         String authorizationHeader =
            requestContext.getHeaderString(UserSessionKeeper.AUTH_TOKEN_KEY);

        // Check if the HTTP Authorization header is present and formatted correctly
         if (authorizationHeader == null) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        // Extract the token from the HTTP Authorization header        
        String token = authorizationHeader; 

        try {
            
            // Validate the token
            validateToken(token);
            
            UserSessionKeeper instance = UserSessionKeeper.getInstance();
            User user = instance.getUserForToken(token);
             
            String scheme = requestContext.getUriInfo().getRequestUri().getScheme();
            requestContext.setSecurityContext(new CdbApiSecurityContext(user, scheme));

        } catch (Exception e) {
             throw new NotAuthorizedException("The user auhorization token could not be validated.");
        }
    }

    private void validateToken(String token) throws Exception {
        
        
        UserSessionKeeper instance = UserSessionKeeper.getInstance();
        if(!instance.validateToken(token)) {
            throw new Exception("Token not valid"); 
        }
    }
}
  
/**
 * Jersey HTTP Basic Auth filter
 * @author Deisss (LGPLv3)
 
@Provider
@PreMatching
public class AuthFilter implements ContainerRequestFilter {
    
    @EJB
    private UserInfoFacade userFacade;
    
    /**
     * Apply the filter : check input request, validate or not with user auth
     * @param containerRequest The request from Tomcat server
     */ /*
    @Override
    public void filter(ContainerRequestContext containerRequest) throws WebApplicationException {
        //GET, POST, PUT, DELETE, ...
        String method = containerRequest.getMethod();
        // myresource/get/56bCA for example
        String path = containerRequest.getUriInfo().getPath(true);
  
        //We do allow wadl to be retrieve
        if(method.equals("GET")){
            return;
        } else if (true){
            return;
        }
  
        //Get the authentification passed in HTTP headers parameters
        String auth = containerRequest.getHeaderString("authorization");
  
        //If the user does not have the right (does not provide any HTTP Basic Auth)
        if(auth == null) {
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
  
        //lap : loginAndPassword
        //String[] lap = BasicAuth.decode(auth);
        String[] lap = auth.split(":"); 
  
        //If login or password fail
        if(lap == null || lap.length != 2) {
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
  
        //DO YOUR DATABASE CHECK HERE (replace that line behind)...
        //User authentificationResult =  AuthentificationThirdParty.authentification(lap[0], lap[1]);
        String username = lap[0]; 
        String password = lap[1];
        UserInfo userInfo = userFacade.findByUsername(username); 
        boolean authenticated = LoginController.validateCredentials(userInfo, password);
  
        //Our system refuse login and password
        if(!authenticated) {
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
  
        // We configure your Security Context here
        String scheme = containerRequest.getUriInfo().getRequestUri().getScheme();
        User user = User.createFromUserInfo(userInfo); 
        containerRequest.setSecurityContext(new CdbApiSecurityContext(user, scheme));
 
        //TODO : HERE YOU SHOULD ADD PARAMETER TO REQUEST, TO REMEMBER USER ON YOUR REST SERVICE...
    }

}*/
