/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.UserGroupFacade;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;


/**
 *
 * @author darek
 */
public class UserGroupControllerUtility extends CdbEntityControllerUtility<UserGroup, UserGroupFacade> {

    @Override
    protected UserGroupFacade getEntityDbFacade() {
        return UserGroupFacade.getInstance();
    }

    @Override
    public String getEntityInstanceName(UserGroup entity) {
        if (entity != null) {
            return entity.getName(); 
        }
        return ""; 
    }
        
    @Override
    public String getDisplayEntityTypeName() {
        return "Registered User Group";
    }

    @Override
    public String getEntityTypeName() {
        return "userGroup";
    }

    @Override
    public UserGroup createEntityInstance(UserInfo sessionUser) {
        return new UserGroup();
    }

    
}
