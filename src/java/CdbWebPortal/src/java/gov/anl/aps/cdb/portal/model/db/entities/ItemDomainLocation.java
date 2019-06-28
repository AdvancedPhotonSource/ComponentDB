/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainLocationController;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "item")
@DiscriminatorValue(value = ItemDomainName.LOCATION_ID + "")
public class ItemDomainLocation extends Item {

    @Override
    public Item createInstance() {
        return new ItemDomainLocation();
    }

    @Override
    public ItemController getItemDomainController() {
        return ItemDomainLocationController.getInstance(); 
    }
    
}
