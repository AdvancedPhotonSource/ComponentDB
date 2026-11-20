/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeMetadata;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class PropertyTypeMetadataFacade extends CdbEntityFacade<PropertyTypeMetadata> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PropertyTypeMetadataFacade() {
        super(PropertyTypeMetadata.class);
    }
    
    public static PropertyTypeMetadataFacade getInstance() {
        return (PropertyTypeMetadataFacade) SessionUtility.findFacade(PropertyTypeMetadataFacade.class.getSimpleName()); 
    }
    
}
