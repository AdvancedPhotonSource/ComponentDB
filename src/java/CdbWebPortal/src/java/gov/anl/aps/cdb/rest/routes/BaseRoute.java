/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.AuthorizationError;
import gov.anl.aps.cdb.portal.controllers.LoginController;
import gov.anl.aps.cdb.portal.model.db.beans.UserInfoFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.AuthorizationUtility;
import gov.anl.aps.cdb.rest.authentication.User;
import java.security.Principal;
import javax.ejb.EJB;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
public abstract class BaseRoute {

    @Context
    SecurityContext securityContext;

    @EJB
    UserInfoFacade userFacade;
    
    private static final Logger LOGGER = LogManager.getLogger(BaseRoute.class.getName());        

    protected boolean isUserAdmin(UserInfo userInfo) {
        String username = userInfo.getUsername();
        return LoginController.isAdmin(username, userFacade);
    }

    protected boolean isUserMaintainer(UserInfo userInfo) {
        String username = userInfo.getUsername();
        return LoginController.isMaintainer(username, userFacade);
    }
    
    protected UserInfo verifyCurrentUserPermissionForItem(Item item) throws AuthorizationError {
        UserInfo updatedByUser = getCurrentRequestUserInfo();
        
        if (!verifyUserPermissionForItem(updatedByUser, item)) {            
            AuthorizationError ex = new AuthorizationError("User does not have permission to update the item");
            LOGGER.error(ex);
            throw ex; 
        }
        
        return updatedByUser; 
    }   

    protected boolean verifyUserPermissionForItem(UserInfo user, Item item) {
        if (user != null) {
            if (isUserAdmin(user)) {
                return true;
            }
            if (isUserMaintainer(user)) {
                return true;
            }
            return AuthorizationUtility.isEntityWriteableByUser(item, user);
        }
        return false;
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
