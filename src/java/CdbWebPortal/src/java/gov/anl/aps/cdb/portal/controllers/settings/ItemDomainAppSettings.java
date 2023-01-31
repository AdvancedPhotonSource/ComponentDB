/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemDomainAppController;

/**
 *
 * @author djarosz
 */
public class ItemDomainAppSettings extends ItemSettings<ItemDomainAppController> {
    
    public ItemDomainAppSettings(ItemDomainAppController parentController) {
        super(parentController);
        displayNumberOfItemsPerPage = 10; 
    }
    
}
