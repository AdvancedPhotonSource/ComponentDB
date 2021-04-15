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

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemDomainCableDesignFacade extends ItemFacadeBase<ItemDomainCableDesign> {
    
    @Override
    public String getDomainName() {
        return ItemDomainName.cableDesign.getValue();
    }
    
    public ItemDomainCableDesignFacade() {
        super(ItemDomainCableDesign.class);
    }
    
    public static ItemDomainCableDesignFacade getInstance() {
        return (ItemDomainCableDesignFacade) SessionUtility.findFacade(ItemDomainCableDesignFacade.class.getSimpleName()); 
    }
    
    public ItemDomainCableDesign edit(ItemDomainCableDesign entity) {
        
        ItemDomainCableDesign result = super.edit(entity);
        
        for (ItemElementRelationship relationship : entity.getDeletedRelationshipList()) {
            ItemElementRelationshipFacade.getInstance().remove(relationship);
        }
        
        return result;
    }

    public List<ItemDomainCableDesign> findByName(String name) {
        return findByDomainAndName(getDomainName(), name);
    }  
}
