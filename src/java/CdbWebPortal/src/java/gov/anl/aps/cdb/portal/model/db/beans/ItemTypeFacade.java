/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
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
    
}
