/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemDomainCableDesignFacade extends ItemFacadeBase<ItemDomainCableDesign> {
    
    @Override
    public ItemDomainName getDomain() {
        return ItemDomainName.cableDesign;
    }
    
    public ItemDomainCableDesignFacade() {
        super(ItemDomainCableDesign.class);
    }
    
    public static ItemDomainCableDesignFacade getInstance() {
        return (ItemDomainCableDesignFacade) SessionUtility.findFacade(ItemDomainCableDesignFacade.class.getSimpleName()); 
    }
    
    /**
     * Updates cable design item.  Overridden here because, if we edit a cable
     * to remove cable relationship objects they are not removed from the database
     * by updating the database for that cable.  Thus they are removed explicitly
     * here.
     */
    @Override
    public ItemDomainCableDesign edit(ItemDomainCableDesign entity) {
        
        ItemDomainCableDesign result = super.edit(entity);
        
        for (ItemElementRelationship ier : entity.getDeletedIerList()) {
            ItemElementRelationshipFacade.getInstance().remove(ier);
        }
        entity.clearDeletedIerList();
        
        return result;
    } 

    @Override
    public List<ItemDomainCableDesign> searchEntities(String searchString) {
        try {            
            return (List<ItemDomainCableDesign>) em.createNamedStoredProcedureQuery("itemCableDesign.searchItems")                    
                    .setParameter("search_string", searchString)
                    .setParameter("limit_row", SEARCH_RESULT_LIMIT)
                    .getResultList();
        } catch (NoResultException ex) {

        }
        return null;
    }

    public List<ItemDomainCableDesign> findByName(String name) {
        return findByDomainAndName(getDomainName(), name);
    }  
}
