/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.authentication;

import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 *
 * @author djarosz
 */
public class UserSessionKeeper {
    
    public static final String AUTH_TOKEN_KEY = "token";     
    
    private static UserSessionKeeper instance = null; 
    
    HashMap<String, User> userTokenMap;
    
    public int ctr; 
    
    private UserSessionKeeper() {
        ctr = 0; 
        userTokenMap = new HashMap<>(); 
    }
    
    public static synchronized UserSessionKeeper getInstance() {
        if (instance == null) {
            instance = new UserSessionKeeper(); 
        }
        return instance;   
    }
    
    public String getToken(UserInfo user) {
        String token = UUID.randomUUID().toString();
        User sessionUser = User.createFromUserInfo(user, token);                
        
        userTokenMap.put(token, sessionUser);                        
        
        return token; 
    }
    
    public boolean revokeToken(String token) {
        User remove = userTokenMap.remove(token);
        return remove != null; 
    }
    
    public User getUserForToken(String token) {
        return userTokenMap.get(token);
    }
    
    public boolean validateToken(String token) {
        User user = userTokenMap.get(token);
        if (user != null) {
            long expirationDate = user.getExpirationDate();
            Date currentDate = new Date();
            long currentTime = currentDate.getTime();
            
            if (currentTime < expirationDate) {
                return true; 
            } else {
                userTokenMap.remove(token); 
            }
        }
        return false;         
    }
    
    public String getNext() {
        ctr = ctr + 1;
        return "Next one is: " + ctr;
    }
    
    
}
