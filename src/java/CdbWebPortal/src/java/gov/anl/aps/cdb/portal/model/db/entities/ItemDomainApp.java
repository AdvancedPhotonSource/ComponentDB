/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainAppControllerUtility;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 *
 * @author djarosz
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.APPS_ID + "")
public class ItemDomainApp extends Item {

    public ItemDomainApp() {
    }
    
    @Override
    public Item createInstance() {
        return new ItemDomainApp(); 
    } 
    
    @Override
    public ItemDomainAppControllerUtility getItemControllerUtility() {
        return new ItemDomainAppControllerUtility(); 
    }
}
