/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class EntityTypeFacade extends CdbEntityFacade<EntityType> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EntityTypeFacade() {
        super(EntityType.class);
    }
    
    public EntityType findByName(String name) {
        try{
            return (EntityType) em.createNamedQuery("EntityType.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
            
        }
        return null;
    }
    
}
