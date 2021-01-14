/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.UserRoleFacade;
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
    
}
