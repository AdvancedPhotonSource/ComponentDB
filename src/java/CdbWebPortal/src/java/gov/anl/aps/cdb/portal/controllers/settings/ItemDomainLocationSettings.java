/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemDomainLocationController;

/**
 *
 * @author djarosz
 */
public class ItemDomainLocationSettings extends ItemSettings<ItemDomainLocationController> {
    
    public ItemDomainLocationSettings(ItemDomainLocationController parentController) {
        super(parentController);
        displayNumberOfItemsPerPage = 25;
        displayDescription = true;
        displayItemListTreeView = true;
    }
    
}
