/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.portal.controllers.LoginController;
import gov.anl.aps.cdb.portal.model.db.beans.UserInfoFacade;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.rest.authentication.User;
import java.security.Principal;
import javax.ejb.EJB;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;


/**
 *
 * @author djarosz
 */
public abstract class BaseRoute {
    
    @Context
    SecurityContext securityContext;   
    
    @EJB
    UserInfoFacade userFacade; 
    
    protected boolean isUserAdmin(UserInfo userInfo) {
        String username = userInfo.getUsername();
        return LoginController.isAdmin(username, userFacade);
    }
    
    protected boolean isUserMaintainer(UserInfo userInfo) {
        String username = userInfo.getUsername();
        return LoginController.isMaintainer(username, userFacade);
    }
    
    protected boolean isCurrentRequestUserAdmin() {
        UserInfo currentRequestUserInfo = getCurrentRequestUserInfo();
        return isUserAdmin(currentRequestUserInfo); 
    }
    
    protected UserInfo getCurrentRequestUserInfo() {
        Principal userPrincipal = securityContext.getUserPrincipal();
        if (userPrincipal instanceof User) {
            UserInfo user = ((User) userPrincipal).getUser();
            return user;
        }
        return null;
    }
    
}
