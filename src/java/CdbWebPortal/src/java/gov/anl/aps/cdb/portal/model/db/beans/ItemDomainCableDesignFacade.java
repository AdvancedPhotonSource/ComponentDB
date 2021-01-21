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
    
    public void editEntitiesAndDestroyRelationships(
            List<ItemDomainCableDesign> entities,
            List<ItemElementRelationship> relationships) {
        
        for (ItemDomainCableDesign entity : entities) {
            edit(entity);
        }
        
        for (ItemElementRelationship relationship : relationships) {
            ItemElementRelationshipFacade.getInstance().remove(relationship);
        }
    }

}
