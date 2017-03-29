/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.constants.InventoryBillOfMaterialItemStates;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * The purpose of this class is to store information required to create an
 * assembly inventory item.
 */
public class InventoryBillOfMaterialItem {

    private final String INVENTORY_DOMAIN_CONTROLLER_NAME = "itemDomainInventoryController";

    // Defined as a constant in InventoryBillOfMaterialItemStates
    protected String state = null;

    // new item to be linked to placeholder (Item Element of instance).
    protected ItemDomainInventory inventoryItem = null;

    protected ItemDomainInventory prevInventoryItem = null;

    // a reference to the parent item instance which includes this as a bom item.
    protected ItemDomainInventory parentItemInstance = null;

    // The catalog item that will be used to create the intventory item. 
    protected ItemElement catalogItemElement = null;
    
    protected ItemElement inventoryItemElement = null; 

    // an event needs to be processon state change. SelectOneButton does not support this.
    protected ItemDomainInventoryController itemDomainInventoryController = null;

    protected boolean applyPermissionToAllNewParts = false;

    private ItemDomainCatalog catalogItem = null;

    protected DataModel existingInventoryItemSelectDataModel = null;
    
    public InventoryBillOfMaterialItem(ItemElement catalogItemElement, ItemDomainInventory parentItemInstance) {        
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
                inventoryItem = (ItemDomainInventory) inventoryItemElement.getContainedItem();
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
            if (catalogItemElement.getIsRequired()) {
                this.state = InventoryBillOfMaterialItemStates.placeholder.getValue();
            } else {
                this.state = InventoryBillOfMaterialItemStates.unspecifiedOptional.getValue(); 
            }
        }

        this.catalogItemElement = catalogItemElement;
        this.parentItemInstance = parentItemInstance;
    }

    public InventoryBillOfMaterialItem(ItemDomainInventory inventoryItem) {
        this.loadItemDomainInventoryController();
        if (itemDomainInventoryController.isItemExistInDb(inventoryItem)) {
            this.state = InventoryBillOfMaterialItemStates.existingItem.getValue();
        } else {
            this.state = InventoryBillOfMaterialItemStates.newItem.getValue();
        }
        this.inventoryItem = inventoryItem;

        // Set default tag
        setDefaultValuesForInventoryItem();
    }

    private void setDefaultValuesForInventoryItem() {
        setDefaultProject();
        setDefaultTag();
    }

    private void setDefaultProject() {
        if (inventoryItem != null) {
            ItemDomainCatalog catalogItem = getCatalogItem();
            if (catalogItem != null) {
                if (catalogItem.getItemProjectList() != null
                        & !catalogItem.getItemProjectList().isEmpty()) {
                    List<ItemProject> catalogItemProjectList = catalogItem.getItemProjectList();
                    inventoryItem.setItemProjectList(new ArrayList<>(catalogItemProjectList));
                }
            }
        }
    }

    private void setDefaultTag() {
        if (inventoryItem != null) {
            if (inventoryItem.getName() == null || inventoryItem.getName().isEmpty()) {
                this.loadItemDomainInventoryController();
                int newItemCount = itemDomainInventoryController.getNewItemCount(getCatalogItem());
                if (newItemCount > 0) {
                    // Remove this item from the count. 
                    newItemCount -= 1;
                }
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

        // Chache prev inventory item. 
        if (prevState.equals(state) == false) {
            if (state.equals(InventoryBillOfMaterialItemStates.newItem.getValue())) {
                prevInventoryItem = inventoryItem;
            }
        }

        this.state = state;
        this.loadItemDomainInventoryController();
        itemDomainInventoryController.changeBillOfMaterialsState(this, prevState);

        // Restore prev inventory item.
        if (prevState.equals(state) == false) {
            if (state.equals(InventoryBillOfMaterialItemStates.unspecifiedOptional.getValue())) {
                inventoryItem = null; 
                prevInventoryItem = null; 
            } else if (prevState.equals(InventoryBillOfMaterialItemStates.newItem.getValue())) {
                inventoryItem = prevInventoryItem;
            }
        }

    }

    public String getStateSelection() {
        if (!state.equals(InventoryBillOfMaterialItemStates.newItem.getValue())) {
            return InventoryBillOfMaterialItemStates.existingItem.getValue();
        } else {
            return state;
        }
    }

    public void setStateSelection(String state) {
        if (!state.equals(InventoryBillOfMaterialItemStates.newItem.getValue())) {
            setState(state);
            updateStateBasedOnCurrentInventoryItem();
        } else {
            setState(state);
        }
    }

    public void loadItemDomainInventoryController() {
        if (itemDomainInventoryController == null) {
            itemDomainInventoryController = (ItemDomainInventoryController) SessionUtility.findBean(INVENTORY_DOMAIN_CONTROLLER_NAME);
        }
    }

    public ItemDomainCatalog getCatalogItem() {
        if (catalogItem == null) {
            if (catalogItemElement == null) {
                if (inventoryItem != null) {
                    catalogItem = inventoryItem.getCatalogItem();
                }
            } else {
                catalogItem = (ItemDomainCatalog) catalogItemElement.getContainedItem();
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

    public ItemElement getInventoryItemElement() {
        if (inventoryItemElement == null) {
            if (parentItemInstance != null && parentItemInstance.getFullItemElementList() != null) {
                for (ItemElement inventoryItemElementItr : parentItemInstance.getFullItemElementList()) {
                    ItemElement catalogItemElementItr = inventoryItemElementItr.getDerivedFromItemElement(); 
                    if (ObjectUtility.equals(catalogItemElementItr, this.catalogItemElement)) {
                        this.inventoryItemElement = inventoryItemElementItr; 
                    }
                }
            }
        }
        return inventoryItemElement;
    }

    // Creates a bill of materials list based on the catalog item and assigns it to the instance item. 
    public static void setBillOfMaterialsListForItem(ItemDomainInventory parentItemInstance, InventoryBillOfMaterialItem containedInBOM) {
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

    public boolean isOptional() {
        if (catalogItemElement != null) {
            return catalogItemElement.getIsRequired() == false;
        }
        return false; 
    }

    public boolean isApplyPermissionToAllNewParts() {
        return applyPermissionToAllNewParts;
    }

    public void setApplyPermissionToAllNewParts(boolean applyPermissionToAllNewParts) {
        this.applyPermissionToAllNewParts = applyPermissionToAllNewParts;
    }

    public ItemDomainInventory getParentItemInstance() {
        return parentItemInstance;
    }

    public ItemDomainInventory getInventoryItem() {
        return inventoryItem;
    }
    
    public void setInventoryItem(ItemDomainInventory inventoryItem) {
        this.inventoryItem = inventoryItem;
        //Set default tag
        if (inventoryItem != null) {
            if (inventoryItem.getId() == null) {
                setDefaultValuesForInventoryItem();
            }
        }
        updateStateBasedOnCurrentInventoryItem();
    }

    private void updateStateBasedOnCurrentInventoryItem() {
        if (inventoryItem == null) {
            if (state.equals(InventoryBillOfMaterialItemStates.unspecifiedOptional.getValue())) {
                return;
            }
            setState(InventoryBillOfMaterialItemStates.placeholder.toString());
        } else {
            loadItemDomainInventoryController();
            if (itemDomainInventoryController.isItemExistInDb(inventoryItem)) {
                setState(InventoryBillOfMaterialItemStates.existingItem.toString());
            } else {
                setState(InventoryBillOfMaterialItemStates.newItem.toString());
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
                List<ItemDomainInventory> ItemInventoryItemList = getCatalogItem().getInventoryItemList();
                // Copy list to not update actual derived from item list. 
                List<ItemDomainInventory> inventoryItemList = new ArrayList<>(ItemInventoryItemList);
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

    public int getExistingInventoryItemCountExistingItems() {
        DataModel itemListDataModel = getExistingInventoryItemSelectDataModel();
        if (itemListDataModel != null) {
            return itemListDataModel.getRowCount();
        }

        return 0;
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
                response += " - [" + inventoryItem.getName() + "]";
            }
        } else {
            response += " - [ ]";
        }

        return response;
    }

}
