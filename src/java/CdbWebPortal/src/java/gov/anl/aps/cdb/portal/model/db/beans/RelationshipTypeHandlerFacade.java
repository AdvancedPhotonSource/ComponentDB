/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.RelationshipTypeHandler;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class RelationshipTypeHandlerFacade extends CdbEntityFacade<RelationshipTypeHandler> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RelationshipTypeHandlerFacade() {
        super(RelationshipTypeHandler.class);
    }
    
}
