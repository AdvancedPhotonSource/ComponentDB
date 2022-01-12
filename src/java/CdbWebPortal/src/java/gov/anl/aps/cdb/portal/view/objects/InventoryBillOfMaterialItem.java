/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.constants.InventoryBillOfMaterialItemStates;
import gov.anl.aps.cdb.portal.controllers.ItemElementController;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCableInventoryControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainInventoryBaseControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainInventoryControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalogBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventoryBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * The purpose of this class is to store information required to create an
 * assembly inventory item.
 */
public class InventoryBillOfMaterialItem {

    // Defined as a constant in InventoryBillOfMaterialItemStates
    protected String state = null;

    // new item to be linked to placeholder (Item Element of instance).
    protected ItemDomainInventoryBase inventoryItem = null;

    protected ItemDomainInventoryBase prevInventoryItem = null;

    // a reference to the parent item instance which includes this as a bom item.
    protected ItemDomainInventoryBase parentItemInstance = null;

    // The catalog item that will be used to create the intventory item. 
    protected ItemElement catalogItemElement = null;

    protected ItemElement inventoryItemElement = null;

    // an event needs to be processon state change. SelectOneButton does not support this.
    protected ItemDomainInventoryControllerUtility itemDomainInventoryController = null;
    protected ItemDomainCableInventoryControllerUtility itemDomainCableInventoryController = null; 

    protected boolean applyPermissionToAllNewParts = false;

    private ItemDomainCatalogBase catalogItem = null;

    protected DataModel existingInventoryItemSelectDataModel = null;

    protected Boolean simpleView = null;

    public Boolean getSimpleView() {
        if (simpleView == null) {
            simpleView = ItemElementController.getInstance().getSettingObject().getGlobalDisplayItemElementSimpleView();
        }
        return simpleView;
    }

    public InventoryBillOfMaterialItem(ItemElement catalogItemElement, ItemDomainInventoryBase parentItemInstance) {
        loadItemDomainInventoryController();
        ItemElement inventoryItemElement = null;
        if (isItemExistInDb(parentItemInstance)) {
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
                inventoryItem = (ItemDomainInventoryBase) inventoryItemElement.getContainedItem();
                if (isItemExistInDb(inventoryItem)) {
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

    public InventoryBillOfMaterialItem(ItemDomainInventoryBase inventoryItem) {        
        if (isItemExistInDb(inventoryItem)) {
            this.state = InventoryBillOfMaterialItemStates.existingItem.getValue();
        } else {
            this.state = InventoryBillOfMaterialItemStates.newItem.getValue();
        }
        this.inventoryItem = inventoryItem;

        // Set default tag
        setDefaultValuesForInventoryItem();
    }
    
    private ItemDomainInventoryBaseControllerUtility getControllerForInventoryItem(ItemDomainInventoryBase inventoryItem) {
        this.loadItemDomainInventoryController();
        if (inventoryItem instanceof ItemDomainInventory) {
            return itemDomainInventoryController;
        } else if (inventoryItem instanceof ItemDomainCableInventory) {
            return itemDomainCableInventoryController;
        }
        return null; 
    }
    
    private boolean isItemExistInDb(ItemDomainInventoryBase inventoryItem) {
        ItemDomainInventoryBaseControllerUtility controller = getControllerForInventoryItem(inventoryItem);
        if (controller != null) {
            return controller.isItemExistInDb(inventoryItem); 
        }        
        
        return false;
    }

    private void setDefaultValuesForInventoryItem() {
        setDefaultProject();
        setDefaultTag();
    }

    private void setDefaultProject() {
        if (inventoryItem != null) {
            if (inventoryItem.getItemProjectList() == null
                    || inventoryItem.getItemProjectList().isEmpty()) {
                ItemDomainCatalogBase catalogItem = getCatalogItem();
                if (catalogItem != null) {
                    if (catalogItem.getItemProjectList() != null
                            && !catalogItem.getItemProjectList().isEmpty()) {
                        List<ItemProject> catalogItemProjectList = catalogItem.getItemProjectList();
                        inventoryItem.setItemProjectList(new ArrayList<>(catalogItemProjectList));
                    }
                }
            }
        }
    }

    /**
     * TODO: I want to change this method to use
     * ItemDomainInventoryControllergenerateItemName(), but I don't really
     * understand everything that is going on here and don't want to break it.
     * There are side-effects with changes to instance variables in the call
     * stack. Also not sure why we call
     * getExistingInventoryItemSelectDataModel(), which gets the inventory list,
     * makes a copy of it, potentially removes an item, then creates a
     * ListDataModel from the resulting list. Are we gaining something by using
     * the data model? Can't we just count the number of items and not copy the
     * list or create the data model?
     */
    private void setDefaultTag() {
        if (inventoryItem != null) {
            if (inventoryItem.getName() == null || inventoryItem.getName().isEmpty()) {
                this.loadItemDomainInventoryController();
                int newItemCount = getNewItemCount(getCatalogItem());
                if (newItemCount > 0) {
                    // Remove this item from the count. 
                    newItemCount -= 1;
                }

                // Add one for user readability. No use of 0 for first item.
                int itemNumber = getExistingInventoryItemSelectDataModel().getRowCount() + newItemCount + 1;
                inventoryItem.setName(ItemDomainInventory.generatePaddedUnitName(itemNumber));
            }
        }
    }

    public List<ItemDomainInventoryBase> getNewItemsToAdd() {
        List<ItemDomainInventoryBase> newItemsToAdd = new ArrayList<>();

        InventoryBillOfMaterialItem parentMostItem = this;
        while (parentMostItem.getParentItemInstance() != null) {
            ItemDomainInventoryBase parentItemInstance = parentMostItem.getParentItemInstance();
            InventoryBillOfMaterialItem containedInBOM = parentItemInstance.getContainedInBOM();
            if (containedInBOM != null) {
                parentMostItem = containedInBOM;
            }
        }

        populateNewItems(newItemsToAdd, parentMostItem);

        return newItemsToAdd;
    }

    private void populateNewItems(List<ItemDomainInventoryBase> newItems, InventoryBillOfMaterialItem bom) {
        ItemDomainInventoryBase inventoryItem = bom.getInventoryItem();

        if (inventoryItem != null) {
            if (bom.state.equals(InventoryBillOfMaterialItemStates.newItem.getValue())) {                
                newItems.add(inventoryItem);                
            }

            List<InventoryBillOfMaterialItem> inventoryDomainBillOfMaterialList = inventoryItem.getInventoryDomainBillOfMaterialList();
            if (inventoryDomainBillOfMaterialList != null) {
                for (InventoryBillOfMaterialItem nextBom : inventoryDomainBillOfMaterialList) {
                    populateNewItems(newItems, nextBom);
                }
            }
        }
    }

    /**
     * Counts new items that will be added for a certain catalog item.
     *
     * @param catalogItem
     * @return count
     */
    public int getNewItemCount(ItemDomainCatalogBase catalogItem) {
        int count = 0;
        List<ItemDomainInventoryBase> newItemsToAdd = getNewItemsToAdd();

        for (ItemDomainInventoryBase item : newItemsToAdd) {
            if (item.getDerivedFromItem() == catalogItem) {
                count++;
            }
        }

        return count;
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
        ItemDomainInventoryBaseControllerUtility controller = getControllerForInventoryItem(this.parentItemInstance); 
        controller.changeBillOfMaterialsState(this, prevState);

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

    public final void loadItemDomainInventoryController() {
        if (itemDomainInventoryController == null) {
            itemDomainInventoryController = new ItemDomainInventoryControllerUtility(); 
        }
        if (itemDomainCableInventoryController == null) {
            itemDomainCableInventoryController = new ItemDomainCableInventoryControllerUtility(); 
        }
    }

    public ItemDomainCatalogBase getCatalogItem() {
        if (catalogItem == null) {
            if (catalogItemElement == null) {
                if (inventoryItem != null) {
                    catalogItem = inventoryItem.getCatalogItem();
                }
            } else {
                catalogItem = (ItemDomainCatalogBase) catalogItemElement.getContainedItem();
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
    public static void setBillOfMaterialsListForItem(ItemDomainInventoryBase parentItemInstance, InventoryBillOfMaterialItem containedInBOM) {
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

    public ItemDomainInventoryBase getParentItemInstance() {
        return parentItemInstance;
    }

    public ItemDomainInventoryBase getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(ItemDomainInventoryBase inventoryItem) {
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
            if (isItemExistInDb(inventoryItem)) {
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
                List<ItemDomainInventoryBase> ItemInventoryItemList = getCatalogItem().getInventoryItemList();
                // Copy list to not update actual derived from item list. 
                List<ItemDomainInventoryBase> inventoryItemList = new ArrayList<>(ItemInventoryItemList);
                if (inventoryItem != null) {                    
                    if (isItemExistInDb(inventoryItem) == false) {
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
            if (getSimpleView()) {
                ItemDomainCatalogBase catalogItem = (ItemDomainCatalogBase) catalogItemElement.getContainedItem();
                if (catalogItem != null) {
                    response += catalogItem.getName();
                } else {
                    response += catalogItemElement.getName();
                }
            } else {
                response += catalogItemElement.getName();
            }
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
