/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.portal.controllers.LoginController;
import gov.anl.aps.cdb.portal.model.db.beans.UserInfoFacade;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
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
    
}
