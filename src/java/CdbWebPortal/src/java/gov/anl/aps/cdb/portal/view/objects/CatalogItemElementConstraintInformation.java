/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */

package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;


public class CatalogItemElementConstraintInformation extends ItemElementConstraintInformation {

    public CatalogItemElementConstraintInformation(ItemElement primaryItemElement) {
        super(primaryItemElement);
    }
    
    private CatalogItemElementConstraintInformation() {   
        super();
    }

    @Override
    protected ItemElementConstraintInformation getNewInstance() {
        return new CatalogItemElementConstraintInformation();
    } 
    
    public boolean isHasInventoryItemAssigned() {
        return itemElement.getContainedItem() != null; 
    }

    @Override
    protected boolean isPreventUpdateContainedItem() {
        if (isHasInventoryItemAssigned()) {
            return true; 
        }
        return false;
    }

    @Override
    protected boolean isPreventsDelete() {
        boolean result = super.isPreventsDelete(); 
        if (!result) {
            Item item = this.getItemElement().getParentItem();
                if (item instanceof ItemDomainCatalog == false) {
                // If contained item is set it also cannot be deleted. 
                return isPreventUpdateContainedItem(); 
            }
        }
        return result; 
    }

    @Override
    public String getRowStyle() {
        if (isPreventsDelete() && isPreventUpdateContainedItem()) {
            return "redRow";
        } else if (isPreventsDelete() || isPreventUpdateContainedItem()){
            return "yellowRow";
        }
        return "greenRow";
    }

    @Override
    public String getRelatedItemElementToString() {
        return "Inventory Element of " + itemElement.getParentItem().getName();
    }
    
}
