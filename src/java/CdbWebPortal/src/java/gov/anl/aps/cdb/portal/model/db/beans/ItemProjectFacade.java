/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemProjectFacade extends CdbEntityFacade<ItemProject> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemProjectFacade() {
        super(ItemProject.class);
    }
    
    public ItemProject findByName(String name) {
        try {
            return (ItemProject) em.createNamedQuery("ItemProject.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }
    
    /**
     * Find unique entity by name.  Returns null if none is found, or raises
     * CdbException if multiple instances are found.
     */
    public ItemProject findUniqueByName(String name, String filterDomainName) throws CdbException {
        
        if ((name == null) || (name.isEmpty())) {
            return null;
        }
        
        // uses findByName() since that method already finds a unique instance by name
        return findByName(name);       
    }
    
    public static ItemProjectFacade getInstance() {
        return (ItemProjectFacade) SessionUtility.findFacade(ItemProjectFacade.class.getSimpleName()); 
    }

}
