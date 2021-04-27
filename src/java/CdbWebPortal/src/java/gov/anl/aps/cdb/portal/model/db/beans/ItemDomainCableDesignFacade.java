/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
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
    
    /**
     * Updates cable design item.  Overridden here because, if we edit a cable
     * to remove cable relationship objects they are not removed from the database
     * by updating the database for that cable.  Thus they are removed explicitly
     * here.
     */
    public ItemDomainCableDesign edit(ItemDomainCableDesign entity) {
        
        ItemDomainCableDesign result = super.edit(entity);
        
        for (ItemConnector connector : entity.getDeletedConnectorList()) {
            ItemConnectorFacade.getInstance().remove(connector);
        }
        entity.clearDeletedConnectorList();
        
        return result;
    }

    public List<ItemDomainCableDesign> findByName(String name) {
        return findByDomainAndName(getDomainName(), name);
    }  
}
