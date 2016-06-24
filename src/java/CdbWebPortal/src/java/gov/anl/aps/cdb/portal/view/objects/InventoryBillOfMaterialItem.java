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
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

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

    protected boolean shownBOM = false;

    protected DataModel existingInventoryItemSelectDataModel = null;

    public InventoryBillOfMaterialItem(ItemElement catalogItemElement, String innitialState, Item parentItemInstance) {
        this.catalogItemElement = catalogItemElement;
        this.state = innitialState;
        this.parentItemInstance = parentItemInstance;
        this.shownBOM = true;             
    }
    
    public InventoryBillOfMaterialItem(Item newItem) {
        this.state = InventoryBillOfMaterialItemStates.newItem.getValue(); 
        this.newItem = newItem; 
        
        if (newItem.getDerivedFromItem() != null) {
            // Set default tag
            if (newItem.getItemIdentifier2() == null || newItem.getItemIdentifier2().isEmpty()) {
                newItem.setItemIdentifier2(newItem.getDerivedFromItem().getName());
            }
        }
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
    public static void setBillOfMaterialsListForItem(Item parentItemInstance, InventoryBillOfMaterialItem containedInBOM) {
        if (parentItemInstance.getInventoryDomainBillOfMaterialList() == null) {
            List<ItemElement> catalogItemElementList = parentItemInstance.getDerivedFromItem().getItemElementDisplayList();

            List<InventoryBillOfMaterialItem> iBillOfMaterialsList = new ArrayList<>();

            for (ItemElement catalogItemElement : catalogItemElementList) {
                InventoryBillOfMaterialItem iBillOfMaterials = new InventoryBillOfMaterialItem(catalogItemElement, InventoryBillOfMaterialItemStates.placeholder.getValue(), parentItemInstance);
                if (containedInBOM != null) {
                    parentItemInstance.setContainedInBOM(containedInBOM);
                }

                iBillOfMaterialsList.add(iBillOfMaterials);
            }

            parentItemInstance.setInventoryDomainBillOfMaterialList(iBillOfMaterialsList);
        }
    }

    public Item getParentItemInstance() {
        return parentItemInstance;
    }

    public Item getNewItem() {
        return newItem;
    }

    public void setNewItem(Item newItem) {        
        //Set default tag
        if (catalogItemElement != null) {
            newItem.setItemIdentifier2(catalogItemElement.getName());
        }
        
        this.newItem = newItem;
    }

    public boolean isShownBOM() {
        return shownBOM;
    }

    public void setShownBOM(boolean shownBOM) {
        this.shownBOM = shownBOM;
    }
    
    public boolean isPartItem() {
        return this.catalogItemElement != null; 
    }
    
    public boolean isRootItem() {
        return !isPartItem(); 
    }

    public DataModel getExistingInventoryItemSelectDataModel() {
        if (existingInventoryItemSelectDataModel == null) {
            List<Item> selectAvailableInventoryList = new ArrayList<>(); 
            for (Item item : catalogItemElement.getContainedItem().getDerivedFromItemList()) {
                if (item.getItemElementMemberList() != null && item.getItemElementMemberList().isEmpty()) {
                    selectAvailableInventoryList.add(item); 
                }
            }
            existingInventoryItemSelectDataModel = new ListDataModel(selectAvailableInventoryList);
        }

        return existingInventoryItemSelectDataModel;
    }

    public void setExistingInventoryItemSelectDataModel(DataModel existingInventoryItemSelectDataModel) {
        this.existingInventoryItemSelectDataModel = existingInventoryItemSelectDataModel;
    }
    
    public String toString() {        
        String response = ""; 
        if (isRootItem()) {
            // Root Item
            response += newItem.getDerivedFromItem().getName(); 
        } else {
            // Part of root item. 
            response += catalogItemElement.getName(); 
        }
        
        // Add simple attributes specified by user. 
        if (newItem != null) {
            // Tag
            if(newItem.getItemIdentifier2() != null && !newItem.getItemIdentifier2().isEmpty()) {
                response += " - " + newItem.getItemIdentifier2();
            }
        }
        
        return response; 
    }
    
    

}
