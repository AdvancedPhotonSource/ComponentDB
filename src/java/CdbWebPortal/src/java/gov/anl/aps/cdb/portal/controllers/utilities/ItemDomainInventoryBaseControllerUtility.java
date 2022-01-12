/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.InventoryBillOfMaterialItemStates;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableInventoryController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryBaseController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalogBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventoryBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableStatusItem;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemStatusUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.InventoryBillOfMaterialItem;
import gov.anl.aps.cdb.portal.view.objects.InventoryStatusPropertyTypeInfo;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 * @param <ItemInventoryEntityType>
 * @param <ItemDomainEntityFacade>
 */
public abstract class ItemDomainInventoryBaseControllerUtility<ItemInventoryEntityType extends ItemDomainInventoryBase, ItemDomainEntityFacade extends ItemFacadeBase<ItemInventoryEntityType>> 
        extends ItemControllerUtility<ItemInventoryEntityType, ItemDomainEntityFacade> implements IItemStatusControllerUtility {           

    private static final Logger logger = LogManager.getLogger(ItemDomainInventoryBaseControllerUtility.class.getName());

    @Override
    public boolean isEntityHasName() {
        return true; 
    }

    @Override
    public boolean isEntityHasQrId() {
        return true; 
    }

    @Override
    public boolean isEntityHasProject() {
        return true; 
    }
    
    @Override
    public void prepareEditInventoryStatus(LocatableStatusItem item, UserInfo sessionUser) {
        ItemStatusUtility.prepareEditInventoryStatus(this, item, sessionUser);
    }

    @Override
    public PropertyValue getItemStatusPropertyValue(LocatableStatusItem item) {
        return ItemStatusUtility.getItemStatusPropertyValue(item); 
    }

    @Override
    public PropertyType getInventoryStatusPropertyType() {
        return ItemStatusUtility.getInventoryStatusPropertyType(this, propertyTypeFacade); 
    }

    @Override
    public InventoryStatusPropertyTypeInfo getInventoryStatusPropertyTypeInfo() {
        return ItemStatusUtility.getInventoryStatusPropertyTypeInfo(this);
    }

    @Override
    public InventoryStatusPropertyTypeInfo initializeInventoryStatusPropertyTypeInfo() {
        return ItemStatusUtility.initializeInventoryStatusPropertyTypeInfo(); 
    }

    @Override
    public ItemInventoryEntityType createEntityInstance(UserInfo sessionUser) {
        ItemInventoryEntityType item = super.createEntityInstance(sessionUser); 
        
        ItemStatusUtility.updateDefaultStatusProperty(item, sessionUser, this);
        
        return item; 
    }      
    
    @Override
    public void prepareEntityInsert(ItemInventoryEntityType item, UserInfo userInfo) throws CdbException {
        if (item.getDerivedFromItem() == null) {
            throw new CdbException("Please specify " + getDerivedFromItemTitle());
        }

        // Restart the element list if an insert failed previously
        ItemElement itemSelfElement = item.getSelfElement();
        item.getFullItemElementList().clear();
        item.getFullItemElementList().add(itemSelfElement);

        List<ItemDomainInventoryBase> newItemsToAdd = getNewItemsToAdd(item);

        if (newItemsToAdd != null) {
            // Clear new item elements for new items. In case a previous insert failed. 
            for (ItemDomainInventoryBase itemToAdd : newItemsToAdd) {
                if (isItemExistInDb(itemToAdd) == false) {
                    //Make sure newest version of display list is fetched.
                    ItemElement selfElement = itemToAdd.getSelfElement();
                    itemToAdd.getFullItemElementList().clear();
                    itemToAdd.getFullItemElementList().add(selfElement);
                    //Make sure display list is updated to reflect changes. 
                    item.resetItemElementDisplayList();
                }
            }
        }
        clearItemElementsForItem(item);
        updatePermissionOnAllNewPartsIfNeeded(newItemsToAdd, item);
        addItemElementsFromBillOfMaterials(item, userInfo);

        super.prepareEntityInsert(item, userInfo);
        checkNewItemsToAdd(item, newItemsToAdd, userInfo);
    }

    private List<ItemDomainInventoryBase> getNewItemsToAdd(ItemDomainInventoryBase item) {
        InventoryBillOfMaterialItem bom = item.getContainedInBOM();
        List<ItemDomainInventoryBase> newItemsToAdd = null;
        if (bom != null) {
            newItemsToAdd = bom.getNewItemsToAdd();
        }
        return newItemsToAdd;
    }        

    @Override
    public void prepareEntityUpdate(ItemInventoryEntityType item, UserInfo userInfo) throws CdbException {
        List<ItemDomainInventoryBase> newItemsToAdd = getNewItemsToAdd(item);
        checkNewItemsToAdd(item, newItemsToAdd, userInfo);
        addItemElementsFromBillOfMaterials(item, userInfo);
        super.prepareEntityUpdate(item, userInfo);
    }

    public void updatePermissionOnAllNewPartsIfNeeded(List<ItemDomainInventoryBase> newItemsToAdd, ItemDomainInventoryBase item) {
        if (isApplyPermissionToAllNewPartsForItem(item)) {
            for (ItemDomainInventoryBase newItem : newItemsToAdd) {
                setPermissionsForItemToCurrentItem(newItem, item);
            }
        }
    }

    public boolean isApplyPermissionToAllNewPartsForItem(ItemDomainInventoryBase item) {
        if (item.getContainedInBOM() != null) {
            return item.getContainedInBOM().isApplyPermissionToAllNewParts();
        }
        return false;
    }

    private void setPermissionsForItemToCurrentItem(ItemDomainInventoryBase inventoryItem, ItemDomainInventoryBase currentItem) {
        if (inventoryItem != currentItem) {
            // Set the permissions to equal. 
            EntityInfo entityInfo = currentItem.getEntityInfo();
            inventoryItem.getEntityInfo().setOwnerUser(entityInfo.getOwnerUser());
            inventoryItem.getEntityInfo().setOwnerUserGroup(entityInfo.getOwnerUserGroup());
            inventoryItem.getEntityInfo().setIsGroupWriteable(entityInfo.getIsGroupWriteable());
        }
    }

    private void checkNewItemsToAdd(ItemInventoryEntityType parent, List<ItemDomainInventoryBase> newItemsToAdd, UserInfo userInfo) throws CdbException {
        if (newItemsToAdd != null && !newItemsToAdd.isEmpty()) {
            for (ItemDomainInventoryBase newItem : newItemsToAdd) {
                // item is checked by default. 
                if (parent != newItem) {
                    if (newItem instanceof ItemDomainCableInventory) {
                        ItemDomainCableInventoryControllerUtility util; 
                        if (this instanceof ItemDomainCableInventoryControllerUtility) {
                            util = (ItemDomainCableInventoryControllerUtility) this; 
                        } else {
                            util = new ItemDomainCableInventoryControllerUtility();   
                        }
                        util.checkItem((ItemDomainCableInventory) newItem);
                    } else {
                        checkItem((ItemInventoryEntityType)newItem);
                    }
                    performPrepareEntityInsertUpdate(newItem, userInfo);
                }                
            }
            // Cross check the nonadded items. 
            checkUniquenessBetweenNewItemsToAdd(newItemsToAdd);
        }
    }

    private void clearItemElementsForItem(ItemDomainInventoryBase item) {
        //Make sure newest version of display list is fetched.
        //Item should be updated using addItemElementsFromBillOfMaterials.
        ItemElement selfElement = item.getSelfElement();
        item.getFullItemElementList().clear();
        item.getFullItemElementList().add(selfElement);
        //Make sure display list is updated to reflect changes. 
        item.resetItemElementDisplayList();
    }

    private void checkUniquenessBetweenNewItemsToAdd(List<ItemDomainInventoryBase> newItemsToAdd) throws CdbException {
        for (int i = 0; i < newItemsToAdd.size(); i++) {
            for (int j = newItemsToAdd.size() - 1; j > -1; j--) {
                if (i == j) {
                    break;
                }
                ItemDomainInventoryBase itemA = newItemsToAdd.get(i);
                ItemDomainInventoryBase itemB = newItemsToAdd.get(j);

                String itemCompareString = itemA.getContainedInBOM().toString() + " and " + itemB.getContainedInBOM().toString();

                if (itemA.getQrId() != null && itemB.getQrId() != null) {
                    if (itemA.getQrId().equals(itemB.getQrId())) {
                        throw new CdbException(itemCompareString + " have QrId: " + itemA.getQrIdDisplay());
                    }
                }

                if (itemA.getDerivedFromItem() == itemB.getDerivedFromItem()) {
                    if (itemA.getItemIdentifier1().equals(itemB.getItemIdentifier1())
                            && itemA.getName().equals(itemB.getName())) {
                        throw new CdbException(itemCompareString + " have same combination of "
                                + getItemIdentifier1Title() + " and " + getNameTitle());
                    }
                }
            }
        }
    }

    public void changeBillOfMaterialsState(InventoryBillOfMaterialItem bomItem, String previousState) {
        if (SessionUtility.runningFaces()) {
            // TODO rewrite the BOM to not require this.
            // Run only within view mode 
            ItemDomainInventoryBaseController controller;
            if (this instanceof ItemDomainInventoryControllerUtility) {
                controller = ItemDomainInventoryController.getInstance();
            } else {
                controller = ItemDomainCableInventoryController.getInstance(); 
            }
            controller.changeBillOfMaterialsState(bomItem, previousState);
        }
    }

    public void addItemElementsFromBillOfMaterials(ItemDomainInventoryBase item, UserInfo sessionUser) throws CdbException {
        // Bill of materials list.
        List<InventoryBillOfMaterialItem> bomItems = item.getInventoryDomainBillOfMaterialList();

        if (bomItems == null) {
            prepareBillOfMaterialsForItem(item);
            bomItems = item.getInventoryDomainBillOfMaterialList();

            // TODO See if this is needed... 
            if (SessionUtility.runningFaces() == false) {
                // API Mode
                // Bill of materials is loaded from updated values set from API to check against db values. 
                Integer id = item.getId();
                if (id != null) {
                    ItemDomainInventoryBase findById = findById(id);
                    List<ItemElement> fullItemElementList = findById.getFullItemElementList();

                    ItemElement selfElement = item.getSelfElement();
                    item.setFullItemElementList(fullItemElementList);
                    item.resetItemElementVars();

                    // Restore self element
                    for (int i = 0; i < fullItemElementList.size(); i++) {
                        ItemElement itemElement = fullItemElementList.get(i);
                        if (itemElement.equals(selfElement)) {
                            fullItemElementList.remove(i);
                            fullItemElementList.add(i, selfElement);
                            break;
                        }
                    }
                }
            }
        }

        if (bomItems != null) {
            for (InventoryBillOfMaterialItem bomItem : bomItems) {
                if (bomItem.getState().equals(InventoryBillOfMaterialItemStates.unspecifiedOptional.getValue())) {
                    continue;
                }

                // Check if current catalog item element already has an item element defined. 
                ItemElement catalogItemElement = bomItem.getCatalogItemElement();
                ItemElement currentInventoryItemElement = null;
                for (ItemElement inventoryItemElement : item.getFullItemElementList()) {
                    ItemElement derivedFromItemElement = inventoryItemElement.getDerivedFromItemElement();
                    if (derivedFromItemElement != null && derivedFromItemElement.equals(catalogItemElement)) {
                        currentInventoryItemElement = inventoryItemElement;
                        logger.debug("Updating element " + currentInventoryItemElement + " to item " + item);
                        break;
                    }
                }

                if (currentInventoryItemElement == null) {                                        
                    currentInventoryItemElement = createItemElement((ItemInventoryEntityType) item, sessionUser);
                    
                    currentInventoryItemElement.init(item, bomItem.getCatalogItemElement(), sessionUser);
                    item.getFullItemElementList().add(currentInventoryItemElement);
                    logger.debug("Creating instance adding element " + currentInventoryItemElement + " to item " + item);
                }

                // User has specified to create a new item for this bill of materials item. 
                String currentBomState = bomItem.getState();
                if (currentBomState.equals(InventoryBillOfMaterialItemStates.newItem.getValue())
                        || currentBomState.equals(InventoryBillOfMaterialItemStates.existingItem.getValue())) {
                    if (bomItem.getInventoryItem() == null) {

                        String actionWord = "defined";
                        if (currentBomState.equals(InventoryBillOfMaterialItemStates.existingItem.getValue())) {
                            actionWord = "selected";
                        }

                        throw new CdbException("An item for: " + bomItem.getCatalogItemElement().getName() + " is not " + actionWord + ".");
                    }

                    ItemDomainInventoryBase inventoryItem = bomItem.getInventoryItem();

                    // No need to do that for existing items. 
                    if (currentBomState.equals(InventoryBillOfMaterialItemStates.newItem.getValue())) {
                        addItemElementsFromBillOfMaterials(inventoryItem, sessionUser);
                        updateContainedItemForElement(currentInventoryItemElement, inventoryItem);
                    } else if (currentBomState.equals(InventoryBillOfMaterialItemStates.existingItem.getValue())) {
                        if (currentInventoryItemElement.getContainedItem() == inventoryItem == false) {
                            ItemDomainInventoryBase dbItem = null;
                            if (inventoryItem instanceof ItemDomainCableInventory) {
                                ItemDomainCableInventoryControllerUtility util = new ItemDomainCableInventoryControllerUtility();
                                dbItem = util.findById(inventoryItem.getId());
                            } else {
                                dbItem = findById(inventoryItem.getId());
                            }
                            updateContainedItemForElement(currentInventoryItemElement, dbItem);
                        }
                    }
                } else if (currentBomState.equals(InventoryBillOfMaterialItemStates.placeholder.getValue())) {
                    currentInventoryItemElement.setContainedItem(null);
                }

                // Use permissions defined in parent of the item for the item element. 
                updateItemElementPermissionsToItem(currentInventoryItemElement, bomItem.getParentItemInstance());
            }
        }

    }

    public void updateContainedItemForElement(ItemElement ie, ItemDomainInventoryBase containedItem) throws CdbException {
        ItemElement derivedFromItemElement = ie.getDerivedFromItemElement();
        Item derivedContainedItem = derivedFromItemElement.getContainedItem();

        ItemDomainCatalogBase catalogItem = containedItem.getCatalogItem();

        if (catalogItem.equals(derivedContainedItem)) {
            ie.setContainedItem(containedItem);
        } else {
            throw new CdbException("Cannot place item of type: " + catalogItem.getName() + " into position for type: " + derivedContainedItem.getName());
        }
    }

    public void updateItemElementPermissionsToItem(ItemElement itemElement, ItemDomainInventoryBase item) {
        EntityInfo entityInfo = item.getEntityInfo();

        itemElement.getEntityInfo().setOwnerUser(entityInfo.getOwnerUser());
        itemElement.getEntityInfo().setOwnerUserGroup(entityInfo.getOwnerUserGroup());
        itemElement.getEntityInfo().setIsGroupWriteable(entityInfo.getIsGroupWriteable());
    }

    public void prepareBillOfMaterialsForItem(ItemDomainInventoryBase item) {
        // Prepare bill of materials if not yet done so.         
        InventoryBillOfMaterialItem iBom = new InventoryBillOfMaterialItem(item);
        InventoryBillOfMaterialItem.setBillOfMaterialsListForItem(item, iBom);
    }
    
}
