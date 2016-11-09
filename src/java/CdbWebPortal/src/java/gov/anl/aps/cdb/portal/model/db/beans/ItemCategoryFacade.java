/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
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
    
}
