/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class PropertyMetadataFacade extends CdbEntityFacade<PropertyMetadata> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PropertyMetadataFacade() {
        super(PropertyMetadata.class);
    }
    
    public static PropertyMetadataFacade getInstance() {
        return (PropertyMetadataFacade) SessionUtility.findFacade(PropertyMetadataFacade.class.getSimpleName()); 
    }
    
}
