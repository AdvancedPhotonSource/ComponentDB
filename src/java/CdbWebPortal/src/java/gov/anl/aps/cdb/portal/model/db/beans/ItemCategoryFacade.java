/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
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
public class ItemCategoryFacade extends CdbEntityFacade<ItemCategory> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemCategoryFacade() {
        super(ItemCategory.class);
    }
    
    public List<ItemCategory> findByDomainName(String domainName) {
        try {
            return (List<ItemCategory>) em.createNamedQuery("ItemCategory.findByDomainName")
                    .setParameter("domainName", domainName)
                    .getResultList();
        } catch (NoResultException ex) {

        }
        return null;
    }

    public static ItemCategoryFacade getInstance() {
        return (ItemCategoryFacade) SessionUtility.findFacade(ItemCategoryFacade.class.getSimpleName()); 
    }    
    
    public ItemCategory findByNameAndDomainName(String name, String domainName) {
        try {
            return (ItemCategory) em.createNamedQuery("ItemCategory.findByNameAndDomainName")
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
    public ItemCategory findUniqueByName(String name, String filterDomainName) throws CdbException {
        
        if ((name == null) || (name.isEmpty())) {
            return null;
        }
        
        // uses findByName() since that method already finds a unique instance by name
        return findByNameAndDomainName(name, filterDomainName);       
    }
}
