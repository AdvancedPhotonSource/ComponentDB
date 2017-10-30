/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemDomainMAARCController;

/**
 *
 * @author djarosz
 */
public class ItemDomainMAARCSettings extends ItemSettings<ItemDomainMAARCController> {
    
    protected Boolean displayItemElementsSimpleView = false; 
    
    public ItemDomainMAARCSettings(ItemDomainMAARCController parentController) {
        super(parentController);
    }
    
    public void toggleSimpleComplexElementsList() {                
        displayItemElementsSimpleView = !displayItemElementsSimpleView; 
    }
    
    public Boolean getDisplayItemElementSimpleView() {
        return displayItemElementsSimpleView; 
    }
    
}
