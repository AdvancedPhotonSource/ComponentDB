/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyMetadataValue;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class AllowedPropertyMetadataValueFacade extends CdbEntityFacade<AllowedPropertyMetadataValue> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AllowedPropertyMetadataValueFacade() {
        super(AllowedPropertyMetadataValue.class);
    }
    
    public static AllowedPropertyMetadataValueFacade getInstance() {
        return (AllowedPropertyMetadataValueFacade) SessionUtility.findFacade(AllowedPropertyMetadataValueFacade.class.getSimpleName()); 
    }
    
}
