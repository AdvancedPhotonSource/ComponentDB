/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.authentication;

import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.security.Principal;
import java.util.Date;
 
public class User implements Principal {
    
    // 30 Day expiration (30 days * ( 24 hours * (60 minues)))     
    public static final long SIXTY_MINUTES = 3600*1000;
    public static final Long EXPIRATION_DURATION = (30 * (24 * (SIXTY_MINUTES)));         
            
    private UserInfo user;
    private Date expirationDate;
    private String token; 
    
    private User() {
        updateExpiration();
    }
    
    public static User createFromUserInfo(UserInfo userInfo, String token) {
        User user = new User();
        user.user = userInfo;
        user.token = token; 
        
        return user; 
    }
    
    public String getRole() {        
        //TODO implement; 
        
        return ""; 
    }
    
    public void updateExpiration() {
        expirationDate = new Date();
        expirationDate = new Date(expirationDate.getTime() + EXPIRATION_DURATION);
    }

    public UserInfo getUser() {
        return user;
    }
    
    public long getExpirationDate() {
        return expirationDate.getTime(); 
    }
 
    @Override
    public String getName() {
        return this.user.getFirstName() + " " + this.user.getLastName();
    }

    public String getToken() {
        return token;
    }
}