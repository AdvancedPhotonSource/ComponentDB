/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.ItemSource;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemSourceFacade extends CdbEntityFacade<ItemSource> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemSourceFacade() {
        super(ItemSource.class);
    }
    
    public static ItemSourceFacade getInstance() {
        return (ItemSourceFacade) SessionUtility.findFacade(ItemSourceFacade.class.getSimpleName()); 
    }
    
}
