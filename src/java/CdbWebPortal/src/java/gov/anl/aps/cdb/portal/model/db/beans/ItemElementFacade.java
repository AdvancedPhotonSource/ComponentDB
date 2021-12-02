/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
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
public class ItemElementFacade extends CdbEntityFacade<ItemElement> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;
    
    protected final Integer SEARCH_RESULT_LIMIT = 1000;

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
    
    public static ItemElementFacade getInstance() {
        return (ItemElementFacade) SessionUtility.findFacade(ItemElementFacade.class.getSimpleName()); 
    }
    
    @Override
    public List<ItemElement> searchEntities(String searchString) {
        try {
            return (List<ItemElement>) em.createNamedStoredProcedureQuery("itemElement.searchItemElements")
                    .setParameter("search_string", searchString)
                    .setParameter("limit_row", SEARCH_RESULT_LIMIT)
                    .getResultList();
        } catch (NoResultException ex) {

        }
        return null;
    }
    
}
