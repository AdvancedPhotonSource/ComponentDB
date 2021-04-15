/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.UserSetting;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class UserSettingFacade extends CdbEntityFacade<UserSetting> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserSettingFacade() {
        super(UserSetting.class);
    }
    
    public static UserSettingFacade getInstance() {
        return (UserSettingFacade) SessionUtility.findFacade(UserSettingFacade.class.getSimpleName()); 
    }
    
}
