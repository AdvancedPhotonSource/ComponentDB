/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class SettingTypeFacade extends CdbEntityFacade<SettingType> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SettingTypeFacade() {
        super(SettingType.class);
    }
    
    public static SettingTypeFacade getInstance() {
        return (SettingTypeFacade) SessionUtility.findFacade(SettingTypeFacade.class.getSimpleName()); 
    }
    
}
