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
@DiscriminatorValue(value = ItemDomainName.CABLE_ID + "")  
@Schema(name = "ItemDomainCable",
        allOf = Item.class
)
@Deprecated
public class ItemDomainCable extends Item {

    @Override
    public Item createInstance() {
        return new ItemDomainCable(); 
    }        
   
}
