/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.RoleTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.RoleType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/**
 *
 * @author darek
 */
public class RoleTypeControllerUtility extends CdbEntityControllerUtility<RoleType, RoleTypeFacade> {

    @Override
    protected RoleTypeFacade getEntityDbFacade() {
        return RoleTypeFacade.getInstance();
    }

    @Override
    public String getEntityTypeName() {
        return "roleType"; 
    }

    @Override
    public RoleType createEntityInstance(UserInfo sessionUser) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
