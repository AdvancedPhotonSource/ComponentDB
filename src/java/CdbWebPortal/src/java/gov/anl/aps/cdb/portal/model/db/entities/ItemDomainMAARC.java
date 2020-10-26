/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author djarosz
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.MAARC_ID + "")  
@Schema(name = "ItemDomainMAARC",
        allOf = Item.class
)
public class ItemDomainMAARC extends Item {
    
    @Override
    public Item createInstance() {
        return new ItemDomainMAARC(); 
    }        
    
}
