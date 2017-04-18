/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemGenericViewController;

/**
 *
 * @author djarosz
 */
public class ItemGenericViewSettings extends ItemSettings<ItemGenericViewController> {
    
    public ItemGenericViewSettings(ItemGenericViewController parentController) {
        super(parentController);
        displayNumberOfItemsPerPage = 25; 
    }
    
}
