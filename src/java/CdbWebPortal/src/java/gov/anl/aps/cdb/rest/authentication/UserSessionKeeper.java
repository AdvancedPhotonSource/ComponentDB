/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.authentication;

import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import javax.ws.rs.core.NewCookie;

/**
 *
 * @author djarosz
 */
public class UserSessionKeeper {
    
    public static String TOKEN_COOKIE_KEY = "token";     
    
    private static UserSessionKeeper instance = null; 
    
    HashMap<String, User> userTokenMap;
    
    public int ctr; 
    
    private UserSessionKeeper() {
        ctr = 0; 
        userTokenMap = new HashMap<>(); 
    }
    
    public static UserSessionKeeper getInstance() {
        if (instance == null) {
            instance = new UserSessionKeeper(); 
        }
        return instance; 
    }
    
    public NewCookie[] getToken(UserInfo user) {
        String token = UUID.randomUUID().toString();
        User sessionUser = User.createFromUserInfo(user);                
        
        userTokenMap.put(token, sessionUser);        
        
        NewCookie[] userToken = new NewCookie[1]; 
        userToken[0] = new NewCookie(TOKEN_COOKIE_KEY, token); 
        
        return userToken; 
    }
    
    public boolean validateToken(String token) {
        User user = userTokenMap.get(token);
        if (user != null) {
            long expirationDate = user.getExpirationDate();
            Date currentDate = new Date();
            long currentTime = currentDate.getTime();
            
            if (currentTime < expirationDate) {
                user.updateExpiration(); 
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
