/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
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
public class ItemTypeFacade extends CdbEntityFacade<ItemType> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemTypeFacade() {
        super(ItemType.class);
    }
    
    public List<ItemType> findByDomainName(String domainName) {
        return (List<ItemType>) em.createNamedQuery("ItemType.findByDomainName")
                .setParameter("domainName", domainName)
                .getResultList(); 
    }
    
    public static ItemTypeFacade getInstance() {
        return (ItemTypeFacade) SessionUtility.findFacade(ItemTypeFacade.class.getSimpleName()); 
    }
    
    public ItemType findByNameAndDomainName(String name, String domainName) {
        try {
            return (ItemType) em.createNamedQuery("ItemType.findByNameAndDomainName")
                    .setParameter("name", name)
                    .setParameter("domainName", domainName)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }
    
    /**
     * Find unique entity by name.  Returns null if none is found, or raises
     * CdbException if multiple instances are found.
     */
    public ItemType findUniqueByName(String name, String filterDomainName) throws CdbException {
        
        if ((name == null) || (name.isEmpty())) {
            return null;
        }
        
        // uses findByName() since that method already finds a unique instance by name
        return findByNameAndDomainName(name, filterDomainName);       
    }
}
