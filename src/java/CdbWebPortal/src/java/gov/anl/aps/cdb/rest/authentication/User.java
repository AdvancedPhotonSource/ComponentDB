/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.authentication;

import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.security.Principal;
import java.util.Date;
 
public class User implements Principal {
    
    public static final long EXPIRATION_DURATION = 24 * (3600*1000); 
            
    private UserInfo user;
    private Date expirationDate;
    
    private User() {
        updateExpiration();
    }
    
    public static User createFromUserInfo(UserInfo userInfo) {
        User user = new User();
        user.user = userInfo;
        
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
    
    public long getExpirationDate() {
        return expirationDate.getTime(); 
    }
 
    @Override
    public String getName() {
        return this.user.getFirstName() + " " + this.user.getLastName();
    }
}