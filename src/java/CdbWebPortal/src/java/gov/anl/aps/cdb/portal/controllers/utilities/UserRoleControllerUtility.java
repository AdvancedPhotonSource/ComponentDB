/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.UserRoleFacade;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.entities.UserRole;

/**
 *
 * @author darek
 */
public class UserRoleControllerUtility extends CdbEntityControllerUtility<UserRole, UserRoleFacade> {

    @Override
    protected UserRoleFacade getEntityDbFacade() {
        return UserRoleFacade.getInstance(); 
    }
        
    @Override
    public String getEntityTypeName() {
        return "userRole";
    }    

    @Override
    public UserRole createEntityInstance(UserInfo sessionUser) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
