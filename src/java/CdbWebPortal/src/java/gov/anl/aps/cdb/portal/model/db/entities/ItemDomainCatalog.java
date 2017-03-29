/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author djarosz
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.CATALOG_ID + "")  
public class ItemDomainCatalog extends Item {
    
    public List<ItemDomainInventory> getInventoryItemList() {
        return (List<ItemDomainInventory>)(List<?>) super.getDerivedFromItemList(); 
    }        

    @Override
    public Item createInstance() {
        return new ItemDomainCatalog(); 
    }        
   
}
