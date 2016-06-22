/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.portal.constants.InventoryBillOfMaterialItemStates;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.List;

/**
 * The purpose of this class is to store information required to create an
 * inventory item with
 */
public class InventoryBillOfMaterialItem {
    
    private final String INVENTORY_DOMAIN_CONTROLLER_NAME = "itemDomainInventoryController"; 

    // Defined as a constant in InventoryBillOfMaterialItemStates
    protected String state = null;

    // new item to be linked to placeholder (Item Element of instance).
    protected Item newItem = null;
    
    // a reference to the parent item instance which includes this as a bom item.
    protected Item parentItemInstance = null; 

    // The catalog item that will be used to create the intventory item. 
    protected ItemElement catalogItemElement = null;
    
    // an event needs to be processon state change. SelectOneButton does not support this.
    protected ItemDomainInventoryController itemDomainInventoryController = null; 

    public InventoryBillOfMaterialItem(ItemElement catalogItemElement, String innitialState, Item parentItemInstance) {
        this.catalogItemElement = catalogItemElement;
        this.state = innitialState;        
        this.parentItemInstance = parentItemInstance;
        
    }

    public String getState() {               
        return state;
    }

    public void setState(String state) {
        String prevState = this.state; 
        
        this.state = state;
        
        if (itemDomainInventoryController == null) {
            itemDomainInventoryController = (ItemDomainInventoryController) SessionUtility.findBean(INVENTORY_DOMAIN_CONTROLLER_NAME);
        }
        
        itemDomainInventoryController.changeBillOfMaterialsState(this, prevState);
        
    }

    public ItemElement getCatalogItemElement() {
        return catalogItemElement;
    }

    public void setCatalogItemElement(ItemElement catalogItemElement) {
        this.catalogItemElement = catalogItemElement;
    }
    
    // Creates a bill of materials list based on the catalog item and assigns it to the instance item. 
    public static void setBillOfMaterialsListForItem(Item parentItemInstance, Item newItem) {
        if (parentItemInstance.getInventoryDomainBillOfMaterialList() == null) {
            List<ItemElement> catalogItemElementList = parentItemInstance.getDerivedFromItem().getItemElementDisplayList();

            List<InventoryBillOfMaterialItem> iBillOfMaterialsList = new ArrayList<>();
            
            for (ItemElement catalogItemElement : catalogItemElementList) {
                InventoryBillOfMaterialItem iBillOfMaterials = new InventoryBillOfMaterialItem(catalogItemElement, InventoryBillOfMaterialItemStates.placeholder.getValue(), parentItemInstance);
                if (newItem != null) {
                    iBillOfMaterials.setNewItem(newItem);
                }
                
                iBillOfMaterialsList.add(iBillOfMaterials);
            }

            parentItemInstance.setInventoryDomainBillOfMaterialList(iBillOfMaterialsList);
        }
    }
    
    // No new item.
    public static void setBillOfMaterialsListForItem(Item parentItemInstance) {
        setBillOfMaterialsListForItem(parentItemInstance, null);
    }

    public Item getParentItemInstance() {
        return parentItemInstance;
    }
    
    public Item getNewItem() {
        return newItem; 
    }
    
    public void setNewItem(Item newItem) {
        this.newItem = newItem; 
    }

}
