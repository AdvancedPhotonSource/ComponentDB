/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.InventoryBillOfMaterialItemStates;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainInventoryFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.InventoryBillOfMaterialItem;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class ItemDomainInventoryControllerUtility extends ItemDomainInventoryBaseControllerUtility<ItemDomainInventory, ItemDomainInventoryFacade> {

    private static final Logger logger = LogManager.getLogger(ItemDomainInventoryControllerUtility.class.getName());

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.inventory.getValue();
    }

    @Override
    public void prepareEntityInsert(ItemDomainInventory item, UserInfo userInfo) throws CdbException {
        if (item.getDerivedFromItem() == null) {
            throw new CdbException("Please specify " + getDerivedFromItemTitle());
        }

        // Restart the element list if an insert failed previously
        ItemElement itemSelfElement = item.getSelfElement();
        item.getFullItemElementList().clear();
        item.getFullItemElementList().add(itemSelfElement);

        List<ItemDomainInventory> newItemsToAdd = getNewItemsToAdd(item);

        if (newItemsToAdd != null) {
            // Clear new item elements for new items. In case a previous insert failed. 
            for (ItemDomainInventory itemToAdd : newItemsToAdd) {
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

    private List<ItemDomainInventory> getNewItemsToAdd(ItemDomainInventory item) {
        InventoryBillOfMaterialItem bom = item.getContainedInBOM();
        List<ItemDomainInventory> newItemsToAdd = null;
        if (bom != null) {
            newItemsToAdd = bom.getNewItemsToAdd();
        }
        return newItemsToAdd;
    }

    @Override
    public void prepareEntityUpdate(ItemDomainInventory item, UserInfo userInfo) throws CdbException {
        List<ItemDomainInventory> newItemsToAdd = getNewItemsToAdd(item);
        checkNewItemsToAdd(item, newItemsToAdd, userInfo);
        addItemElementsFromBillOfMaterials(item, userInfo);
        super.prepareEntityUpdate(item, userInfo);
    }

    public void updatePermissionOnAllNewPartsIfNeeded(List<ItemDomainInventory> newItemsToAdd, ItemDomainInventory item) {
        if (isApplyPermissionToAllNewPartsForItem(item)) {
            for (ItemDomainInventory newItem : newItemsToAdd) {
                setPermissionsForItemToCurrentItem(newItem, item);
            }
        }
    }

    public boolean isApplyPermissionToAllNewPartsForItem(ItemDomainInventory item) {
        if (item.getContainedInBOM() != null) {
            return item.getContainedInBOM().isApplyPermissionToAllNewParts();
        }
        return false;
    }

    private void setPermissionsForItemToCurrentItem(ItemDomainInventory inventoryItem, ItemDomainInventory currentItem) {
        if (inventoryItem != currentItem) {
            // Set the permissions to equal. 
            EntityInfo entityInfo = currentItem.getEntityInfo();
            inventoryItem.getEntityInfo().setOwnerUser(entityInfo.getOwnerUser());
            inventoryItem.getEntityInfo().setOwnerUserGroup(entityInfo.getOwnerUserGroup());
            inventoryItem.getEntityInfo().setIsGroupWriteable(entityInfo.getIsGroupWriteable());
        }
    }

    private void checkNewItemsToAdd(ItemDomainInventory parent, List<ItemDomainInventory> newItemsToAdd, UserInfo userInfo) throws CdbException {
        if (newItemsToAdd != null && !newItemsToAdd.isEmpty()) {
            for (ItemDomainInventory newItem : newItemsToAdd) {
                // item is checked by default. 
                if (parent != newItem) {
                    checkItem(newItem);
                }
                performPrepareEntityInsertUpdate(newItem, userInfo);
            }
            // Cross check the nonadded items. 
            checkUniquenessBetweenNewItemsToAdd(newItemsToAdd);
        }
    }

    private void clearItemElementsForItem(ItemDomainInventory item) {
        //Make sure newest version of display list is fetched.
        //Item should be updated using addItemElementsFromBillOfMaterials.
        ItemElement selfElement = item.getSelfElement();
        item.getFullItemElementList().clear();
        item.getFullItemElementList().add(selfElement);
        //Make sure display list is updated to reflect changes. 
        item.resetItemElementDisplayList();
    }

    private void checkUniquenessBetweenNewItemsToAdd(List<ItemDomainInventory> newItemsToAdd) throws CdbException {
        for (int i = 0; i < newItemsToAdd.size(); i++) {
            for (int j = newItemsToAdd.size() - 1; j > -1; j--) {
                if (i == j) {
                    break;
                }
                ItemDomainInventory itemA = newItemsToAdd.get(i);
                ItemDomainInventory itemB = newItemsToAdd.get(j);

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
            ItemDomainInventoryController controller = ItemDomainInventoryController.getInstance();
            controller.changeBillOfMaterialsState(bomItem, previousState);
        }
    }

    public void addItemElementsFromBillOfMaterials(ItemDomainInventory item, UserInfo sessionUser) throws CdbException {
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
                    ItemDomainInventory findById = findById(id);
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
                    currentInventoryItemElement = new ItemElement();
                    
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

                    ItemDomainInventory inventoryItem = bomItem.getInventoryItem();

                    // No need to do that for existing items. 
                    if (currentBomState.equals(InventoryBillOfMaterialItemStates.newItem.getValue())) {
                        addItemElementsFromBillOfMaterials(inventoryItem, sessionUser);
                        updateContainedItemForElement(currentInventoryItemElement, inventoryItem);
                    } else if (currentBomState.equals(InventoryBillOfMaterialItemStates.existingItem.getValue())) {
                        if (currentInventoryItemElement.getContainedItem() == inventoryItem == false) {
                            updateContainedItemForElement(currentInventoryItemElement, findById(inventoryItem.getId()));
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

    public void updateContainedItemForElement(ItemElement ie, ItemDomainInventory containedItem) throws CdbException {
        ItemElement derivedFromItemElement = ie.getDerivedFromItemElement();
        Item derivedContainedItem = derivedFromItemElement.getContainedItem();

        ItemDomainCatalog catalogItem = containedItem.getCatalogItem();

        if (catalogItem.equals(derivedContainedItem)) {
            ie.setContainedItem(containedItem);
        } else {
            throw new CdbException("Cannot place item of type: " + catalogItem.getName() + " into position for type: " + derivedContainedItem.getName());
        }
    }

    public void updateItemElementPermissionsToItem(ItemElement itemElement, ItemDomainInventory item) {
        EntityInfo entityInfo = item.getEntityInfo();

        itemElement.getEntityInfo().setOwnerUser(entityInfo.getOwnerUser());
        itemElement.getEntityInfo().setOwnerUserGroup(entityInfo.getOwnerUserGroup());
        itemElement.getEntityInfo().setIsGroupWriteable(entityInfo.getIsGroupWriteable());
    }

    public void prepareBillOfMaterialsForItem(ItemDomainInventory item) {
        // Prepare bill of materials if not yet done so.         
        InventoryBillOfMaterialItem iBom = new InventoryBillOfMaterialItem(item);
        InventoryBillOfMaterialItem.setBillOfMaterialsListForItem(item, iBom);
    }

    @Override
    protected ItemDomainInventoryFacade getItemFacadeInstance() {
        return ItemDomainInventoryFacade.getInstance();
    }

    @Override
    public String getDerivedFromItemTitle() {
        return "Catalog Item";
    }

    @Override
    public String getEntityTypeName() {
        return "componentInstance";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Inventory Item";
    }

    @Override
    public List<ItemDomainInventory> getItemList() {
        return itemFacade.findByDomainOrderByDerivedFromItem(getDefaultDomainName());
    }

    @Override
    protected ItemDomainInventory instenciateNewItemDomainEntity() {
        return new ItemDomainInventory();
    }

    @Override
    public String getStatusPropertyTypeName() {
        return ItemDomainInventory.ITEM_DOMAIN_INVENTORY_STATUS_PROPERTY_TYPE_NAME;
    }
}
