/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.UserRole;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class UserRoleFacade extends CdbEntityFacade<UserRole> {

    @PersistenceContext(unitName = "CdbWebPortalPU"
    
    )
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserRoleFacade() {
        super(UserRole.class);
    }
    
    public static UserRoleFacade getInstance() {
        return (UserRoleFacade) SessionUtility.findFacade(UserRoleFacade.class.getSimpleName()); 
    }
    
}
