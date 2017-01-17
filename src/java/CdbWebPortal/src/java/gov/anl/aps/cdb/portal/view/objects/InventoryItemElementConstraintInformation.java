/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */

package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;


public class InventoryItemElementConstraintInformation extends ItemElementConstraintInformation {

    public InventoryItemElementConstraintInformation(ItemElement primaryItemElement) {
        super(primaryItemElement);
    }
    
    private InventoryItemElementConstraintInformation() {   
        super();
    }

    @Override
    protected ItemElementConstraintInformation getNewInstance() {
        return new InventoryItemElementConstraintInformation();
    }   

    @Override
    protected boolean isPreventsDelete() {
        boolean result = super.isPreventsDelete(); 
        if (!result) {           
            return itemElement.getContainedItem() != null; 
        }
        return result; 
    }
    
}
