/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.view.objects;

import com.sun.javafx.scene.control.skin.VirtualFlow;
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
    protected Item inventoryItem = null;

    // a reference to the parent item instance which includes this as a bom item.
    protected Item parentItemInstance = null;

    // The catalog item that will be used to create the intventory item. 
    protected ItemElement catalogItemElement = null;

    // an event needs to be processon state change. SelectOneButton does not support this.
    protected ItemDomainInventoryController itemDomainInventoryController = null;

    protected boolean applyPermissionToAllNewParts = false;

    private Item catalogItem = null;

    protected DataModel existingInventoryItemSelectDataModel = null;

    public InventoryBillOfMaterialItem(ItemElement catalogItemElement, Item parentItemInstance) {
        loadItemDomainInventoryController();
        ItemElement inventoryItemElement = null;
        if (itemDomainInventoryController.isItemExistInDb(parentItemInstance)) {
            for (ItemElement inventoryElement : parentItemInstance.getFullItemElementList()) {
                if (inventoryElement.getDerivedFromItemElement() != null) {
                    if (inventoryElement.getDerivedFromItemElement() == catalogItemElement) {
                        inventoryItemElement = inventoryElement;
                        break;
                    }
                }
            }
        }

        if (inventoryItemElement != null) {
            if (inventoryItemElement.getContainedItem() != null) {
                inventoryItem = inventoryItemElement.getContainedItem();
                if (itemDomainInventoryController.isItemExistInDb(inventoryItem)) {
                    this.state = InventoryBillOfMaterialItemStates.existingItem.getValue();
                    // No need to display bom for built part. 
                    inventoryItem.setInventoryDomainBillOfMaterialList(null);
                } else {
                    this.state = InventoryBillOfMaterialItemStates.newItem.getValue();
                }
            } else { 
                this.state = InventoryBillOfMaterialItemStates.placeholder.getValue();
            }
        } else {
            this.state = InventoryBillOfMaterialItemStates.placeholder.getValue();
        }
               
        this.catalogItemElement = catalogItemElement;
        this.parentItemInstance = parentItemInstance;
    }

    public InventoryBillOfMaterialItem(Item inventoryItem) {
        this.loadItemDomainInventoryController();
        if (itemDomainInventoryController.isItemExistInDb(inventoryItem)) {
            this.state = InventoryBillOfMaterialItemStates.existingItem.getValue();
        } else {
            this.state = InventoryBillOfMaterialItemStates.newItem.getValue();
        }
        this.inventoryItem = inventoryItem;

        // Set default tag
        setDefaultTag();
    }

    private void setDefaultTag() {
        if (inventoryItem != null) {
            if (inventoryItem.getName() == null || inventoryItem.getName().isEmpty()) {
                this.loadItemDomainInventoryController();
                int newItemCount = itemDomainInventoryController.getNewItemCount(getCatalogItem());
                // Remove this item from the count. 
                newItemCount -= 1;
                // Add one for user readability. No use of 0 for first item. 
                inventoryItem.setName("Unit: " + (getExistingInventoryItemSelectDataModel().getRowCount() + newItemCount + 1) + "");
            }
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        String prevState = this.state;

        this.state = state;
        this.loadItemDomainInventoryController();

        itemDomainInventoryController.changeBillOfMaterialsState(this, prevState);

    }

    public void loadItemDomainInventoryController() {
        if (itemDomainInventoryController == null) {
            itemDomainInventoryController = (ItemDomainInventoryController) SessionUtility.findBean(INVENTORY_DOMAIN_CONTROLLER_NAME);
        }
    }

    public Item getCatalogItem() {
        if (catalogItem == null) {
            if (catalogItemElement == null) {
                if (inventoryItem != null) {
                    catalogItem = inventoryItem.getDerivedFromItem();
                }
            } else {
                catalogItem = catalogItemElement.getContainedItem();
            }
        }
        return catalogItem;
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

            if (containedInBOM != null) {
                parentItemInstance.setContainedInBOM(containedInBOM);
            }

            List<InventoryBillOfMaterialItem> iBillOfMaterialsList = new ArrayList<>();

            for (ItemElement catalogItemElement : catalogItemElementList) {
                InventoryBillOfMaterialItem iBillOfMaterials = new InventoryBillOfMaterialItem(catalogItemElement, parentItemInstance);

                iBillOfMaterialsList.add(iBillOfMaterials);
            }

            parentItemInstance.setInventoryDomainBillOfMaterialList(iBillOfMaterialsList);
        }
    }

    public boolean isApplyPermissionToAllNewParts() {
        return applyPermissionToAllNewParts;
    }

    public void setApplyPermissionToAllNewParts(boolean applyPermissionToAllNewParts) {
        this.applyPermissionToAllNewParts = applyPermissionToAllNewParts;
    }

    public Item getParentItemInstance() {
        return parentItemInstance;
    }

    public Item getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(Item inventoryItem) {
        this.inventoryItem = inventoryItem;
        //Set default tag
        if (inventoryItem != null) {
            if (inventoryItem.getId() == null) {
                setDefaultTag();
            }
        }
    }

    public boolean isPartItem() {
        return this.catalogItemElement != null;
    }

    public boolean isRootItem() {
        return !isPartItem();
    }

    public boolean hasCatalogItem() {
        return getCatalogItem() != null;
    }

    public DataModel getExistingInventoryItemSelectDataModel() {
        if (existingInventoryItemSelectDataModel == null) {
            if (getCatalogItem() != null) {
                List<Item> ItemInventoryItemList = getCatalogItem().getDerivedFromItemList();
                // Copy list to not update actual derived from item list. 
                List<Item> inventoryItemList = new ArrayList<>(ItemInventoryItemList); 
                if (inventoryItem != null) {
                    loadItemDomainInventoryController();
                    if (itemDomainInventoryController.isItemExistInDb(inventoryItem) == false) {
                        if (inventoryItemList.contains(inventoryItem)) {
                            // Remove since it is not yet existing. 
                            inventoryItemList.remove(inventoryItem);
                        }
                    }
                }
                existingInventoryItemSelectDataModel = new ListDataModel(inventoryItemList);
            }
        }

        return existingInventoryItemSelectDataModel;
    }

    public void setExistingInventoryItemSelectDataModel(DataModel existingInventoryItemSelectDataModel) {
        this.existingInventoryItemSelectDataModel = existingInventoryItemSelectDataModel;
    }

    @Override
    public String toString() {
        String response = "";
        if (isRootItem()) {
            // Root Item
            response += inventoryItem.getDerivedFromItem().getName();
        } else {
            // Part of root item. 
            response += catalogItemElement.getName();
        }

        // Add simple attributes specified by user. 
        if (inventoryItem != null) {
            // Tag
            if (inventoryItem.getName() != null && !inventoryItem.getName().isEmpty()) {
                response += " - " + inventoryItem.getName();
            }
        }

        return response;
    }

}
