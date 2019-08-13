/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import java.io.Serializable;

public abstract class ItemTypeCategoryEntity extends CdbEntity implements Serializable {
    
    private transient boolean hasCategories; 
    private transient boolean hasTypes; 

    public abstract Domain getDomain();

    public abstract void setDomain(Domain domain);

    protected String getItemTypeTitle() {
        if (getDomain() != null) {
            return ItemController.getItemItemTypeTitleForDomain(getDomain());
        }
        return "Type";
    }

    protected String getItemCategoryTitle() {
        if (getDomain() != null) {
            return ItemController.getItemItemCategoryTitleForDomain(getDomain());
        }
        return "Category"; 
    }
    
    public boolean getHasCategories() {
        if (this instanceof ItemCategory) {
            return false; 
        }
        if (getDomain() != null) {
            return ItemController.getItemHasItemCategoriesForDomain(getDomain()); 
        }
        return false; 
    }
    
    public boolean getHasTypes() {
        if (this instanceof ItemType) {
            return false; 
        }        
        if (getDomain() != null) {
            return ItemController.getItemHasItemTypesForDomain(getDomain()); 
        }
        return false; 
    }

}
