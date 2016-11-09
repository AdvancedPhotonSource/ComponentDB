/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemElementFacade extends CdbEntityFacade<ItemElement> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemElementFacade() {
        super(ItemElement.class);
    }
    
    public ItemElement findById(Integer id) {
        try {
            return (ItemElement) em.createNamedQuery("ItemElement.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }
    
}
