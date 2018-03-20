/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;

/**
 *
 * @author djarosz
 */
public class ItemDomainMachineDesignSettings extends ItemSettings<ItemDomainMachineDesignController> {        
        
    protected Boolean displayItemElementsSimpleView = false; 
    
    public ItemDomainMachineDesignSettings(ItemDomainMachineDesignController parentController) {
        super(parentController);
    }    
    
    public Boolean getDisplayItemElementSimpleView() {
        return displayItemElementsSimpleView; 
    }
    
}
