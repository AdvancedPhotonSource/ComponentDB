/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.InventoryBillOfMaterialItemStates;
import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainInventoryBaseControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalogBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
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
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author craig
 * @param <InventoryControllerUtility>
 * @param <ItemInventoryBaseDomainEntity>
 * @param <ItemDomainInventoryEntityBaseFacade>
 * @param <ItemInventoryEntityBaseSettingsObject>
 */
public abstract class ItemDomainInventoryBaseController<
            InventoryControllerUtility extends ItemDomainInventoryBaseControllerUtility<ItemInventoryBaseDomainEntity, ItemDomainInventoryEntityBaseFacade>, ItemInventoryBaseDomainEntity extends ItemDomainInventoryBase, ItemDomainInventoryEntityBaseFacade extends ItemFacadeBase<ItemInventoryBaseDomainEntity>, ItemInventoryEntityBaseSettingsObject extends ItemSettings>
        extends ItemController<InventoryControllerUtility, ItemInventoryBaseDomainEntity, ItemDomainInventoryEntityBaseFacade, ItemInventoryEntityBaseSettingsObject> implements IItemStatusController {

    // Inventory status variables
    protected InventoryStatusPropertyTypeInfo inventoryStatusPropertyTypeInfo = null;

    //Variables used for creation of new inventory item.     
    private TreeNode currentItemBOMListTree = null;
    private TreeNode selectedItemBOMTreeNode = null;
    private boolean showOptionalPartsInBom = false;
    private Boolean currentItemBOMTreeHasOptionalItems = null;
    private Integer BOM_CHILD_MIN_COUNT_FOR_SCROLLPANEL = 30;
    private boolean isDisplayBomTreeInScrollpanel = false;

    protected abstract String generatePaddedUnitName(int itemNumber);

    public String generateItemName(
            ItemDomainInventoryBase inventoryItem,
            ItemDomainCatalogBase catalogItem) {
        return generateItemName(inventoryItem, catalogItem, 1);
    }

    public String generateItemName(
            ItemDomainInventoryBase inventoryItem,
            ItemDomainCatalogBase catalogItem,
            int newInstanceCount) {

        int numExistingItems = 0;
        if (catalogItem != null) {
            numExistingItems = catalogItem.getDerivedFromItemList().size();
        }

        int itemNumber = numExistingItems + newInstanceCount;
        return generatePaddedUnitName(itemNumber);
    }

    @Override
    public String prepareCreate() {
        ItemController derivedItemController = getDefaultDomainDerivedFromDomainController();
        if (derivedItemController != null) {
            derivedItemController.getSelectedObjectAndResetDataModel();
            derivedItemController.getSettingObject().clearListFilters();
            derivedItemController.setFilteredObjectList(null);
        }

        String createResult = super.prepareCreate();

        return createResult;
    }

    @Override
    public String getItemDisplayString(Item item) {
        if (item != null) {
            if (item instanceof ItemDomainInventoryBase) {
                if (item.getDerivedFromItem() != null) {
                    String result = item.getDerivedFromItem().getName();

                    //Tag to help user identify the item
                    String tag = item.getName();
                    if (tag != null && !tag.isEmpty()) {
                        result += " - [" + tag + "]";
                    }

                    return result;
                } else {
                    return "No inventory item defied";
                }
            } else {
                return getItemItemController(item).getItemDisplayString(item);
            }
        }
        return null;

    }

    public String getInventoryItemElementDisplayString(ItemElement itemElement) {
        if (itemElement != null) {
            if (itemElement.getContainedItem() != null) {
                Item inventoryItem = itemElement.getContainedItem();
                return getItemDisplayString(inventoryItem);
            }

            ItemDomainCatalogBase catalogItem = getCatalogItemForInventoryItemElement(itemElement);
            if (catalogItem != null) {
                return catalogItem.getName() + "- [ ]";
            } else {
                return "Undefined Part: " + itemElement.getDerivedFromItemElement().getName();
            }
        }
        return null;
    }

    public ItemDomainCatalogBase getCatalogItemForInventoryItemElement(ItemElement inventoryItemElement) {
        if (inventoryItemElement != null) {
            ItemElement derivedFromItemElement = inventoryItemElement.getDerivedFromItemElement();
            if (derivedFromItemElement.getContainedItem() != null) {
                return (ItemDomainCatalogBase) derivedFromItemElement.getContainedItem();
            }
        }
        return null;
    }
    
    @Override
    public ItemInventoryBaseDomainEntity createEntityInstance() {
        return super.createEntityInstance(); //To change body of generated methods, choose Tools | Templates.
    }

    // <editor-fold defaultstate="collapsed" desc="Inventory status implementation">
    @Override
    public final PropertyValue getCurrentStatusPropertyValue() {
        ItemInventoryBaseDomainEntity current = getCurrent();
        return getControllerUtility().getItemStatusPropertyValue(current);
    }

    @Override
    public final void prepareEditInventoryStatus() {
        ItemInventoryBaseDomainEntity current = getCurrent();
        prepareEditInventoryStatus(current);
    }

    public final void prepareEditInventoryStatus(LocatableStatusItem item) {
        UserInfo user = SessionUtility.getUser();
        getControllerUtility().prepareEditInventoryStatus(item, user);
    }

    @Override
    public final PropertyType getInventoryStatusPropertyType() {
        return getControllerUtility().getInventoryStatusPropertyType();
    }

    // </editor-fold>      
    @Override
    public boolean getEntityDisplayImportButton() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemConnectors() {
        return false;
    }

    @Override
    public boolean getRenderedHistoryButton() {
        return ItemStatusUtility.getRenderedHistoryButton(this);
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return true;
    }

    @Override
    public String getNameTitle() {
        return "Tag";
    }

    @Override
    public boolean getEntityDisplayItemGallery() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemLogs() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemSources() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemProperties() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemsDerivedFromItem() {
        return false;
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isAllowedSetDerivedFromItemForCurrentItem() {
        if (getCurrent() != null) {
            return !getCurrent().isIsCloned();
        }

        return false;
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        return false;
    }

    @Override
    public String getStyleName() {
        return "inventory";
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        return null;
    }   

    public List<Item> getSelectionListForSelectedItemElementForUpdate() {
        ItemDomainInventoryBase current = getCurrent();
        ItemElement selectedItemElementForUpdate = current.getSelectedItemElementForUpdate();
        if (selectedItemElementForUpdate != null) {
            ItemElement derivedFromItemElement = selectedItemElementForUpdate.getDerivedFromItemElement();
            Item containedItem = derivedFromItemElement.getContainedItem();
            if (containedItem != null) {
                List<Item> derivedFromItemList = containedItem.getDerivedFromItemList();
                return derivedFromItemList;
            }
        }
        return null;
    }

    @Override
    public void prepareAddItemDerivedFromItem(Item item) {
        isDisplayBomTreeInScrollpanel = getShowInScrollPanel(item);
        super.prepareAddItemDerivedFromItem(item);
        prepareBillOfMaterialsForCurrentItem();
    }

    public void prepareBillOfMaterialsForCurrentItem() {
        ItemDomainInventoryBase current = getCurrent();
        ItemDomainCatalogBase catalogItem = current.getCatalogItem();
        isDisplayBomTreeInScrollpanel = getShowInScrollPanel(catalogItem);
        getControllerUtility().prepareBillOfMaterialsForItem(current);
    }
    
    public Boolean displayBOMEditButton() {
        ItemDomainInventoryBase current = getCurrent();
        if (current != null) {
            List<ItemElement> catalogItemElementDisplayList;
            if (current.getDerivedFromItem() != null) {
                catalogItemElementDisplayList = current.getDerivedFromItem().getItemElementDisplayList();
                return catalogItemElementDisplayList != null && catalogItemElementDisplayList.isEmpty() == false;
            }
        }

        return false;
    }
    
    public String saveEditBOMList() {
        return this.update();
    }

    public void prepareEditBOMForCurrent() {
        resetBOMSupportVariables();
        prepareBillOfMaterialsForCurrentItem();
    }

    @Override
    public void setCurrentDerivedFromItem(Item derivedFromItem) {
        if (getCurrent().getDerivedFromItem() == derivedFromItem) {
            //No need to set it is currently the same catalog item.
            return;
        }
        super.setCurrentDerivedFromItem(derivedFromItem);
        resetBOMSupportVariables();
    }    

    public void resetBOMSupportVariables() {
        // All variables will be genererated when needed. 
        if (getCurrent() != null) {
            getCurrent().setInventoryDomainBillOfMaterialList(null);
            getCurrent().setContainedInBOM(null);
        }
        currentItemBOMListTree = null;
        selectedItemBOMTreeNode = null;
        showOptionalPartsInBom = false;
        currentItemBOMTreeHasOptionalItems = null;

    }

    public TreeNode getCurrentItemBOMListTree() {
        if (currentItemBOMListTree == null) {
            currentItemBOMListTree = buildTreeNodeFromParentItem(getCurrent().getContainedInBOM());
        }

        return currentItemBOMListTree;
    }

    private boolean getShowInScrollPanel(Item catalogItem) {
        if (catalogItem != null) {
            List<ItemElement> itemElementDisplayList = catalogItem.getItemElementDisplayList();
            if (itemElementDisplayList.size() > BOM_CHILD_MIN_COUNT_FOR_SCROLLPANEL) {
                return true;
            }
        }

        return false;
    }

    public boolean isIsDisplayBomTreeInScrollpanel() {
        return isDisplayBomTreeInScrollpanel;
    }

    private TreeNode buildTreeNodeFromParentItem(InventoryBillOfMaterialItem startingBOM) {
        return buildTreeNodeFromParentItem(null, null, startingBOM);
    }

    private TreeNode buildTreeNodeFromParentItem(TreeNode root, TreeNode parent, InventoryBillOfMaterialItem nextBOM) {
        // The tree needs to be created. 
        if (root == null) {
            root = new DefaultTreeNode(null, null);
            parent = new DefaultTreeNode(nextBOM, root);
            parent.setExpanded(true);
            parent.setType(nextBOM.getState());
        }

        /*
        if (updateSelectedPointer) {
            if (parent.getData() == selectedItemBOMTreeNode.getData()) {
                selectedItemBOMTreeNode = parent; 
                updateSelectedPointer = false; 
            }
        }
         */
        if (nextBOM.getInventoryItem() != null) {
            List<InventoryBillOfMaterialItem> nextItemBOMList;
            nextItemBOMList = nextBOM.getInventoryItem().getInventoryDomainBillOfMaterialList();

            if (nextItemBOMList != null && nextItemBOMList.isEmpty() == false) {
                for (InventoryBillOfMaterialItem iBOM : nextItemBOMList) {
                    TreeNode newNode = new DefaultTreeNode(iBOM, parent);
                    newNode.setExpanded(true);
                    newNode.setType(iBOM.getState());
                    buildTreeNodeFromParentItem(root, newNode, iBOM);
                }
            }
        }

        return root;

    }

    public void addNewChildrenToCurrentSelection() {
        buildTreeNodeFromParentItem(currentItemBOMListTree,
                selectedItemBOMTreeNode,
                (InventoryBillOfMaterialItem) selectedItemBOMTreeNode.getData());
    }

    public TreeNode getSelectedItemBOMTreeNode() {
        return selectedItemBOMTreeNode;
    }

    public void setSelectedItemBOMTreeNode(TreeNode selectedItemBOMTreeNode) {
        if (selectedItemBOMTreeNode == null) {
            // Tree will set to null on every form update. 
            return;
        }

        // Clear selection in case same type of component item is in parts list.
        selectedObject = null;

        // Clear Filters
        settingObject.clearListFilters();
        filteredObjectList = null;

        this.selectedItemBOMTreeNode = selectedItemBOMTreeNode;

    }

    public boolean isRenderBomOptionalUnspecified(InventoryBillOfMaterialItem billOfMaterialsItem) {
        if (billOfMaterialsItem != null) {
            return billOfMaterialsItem.getState().equals(InventoryBillOfMaterialItemStates.unspecifiedOptional.getValue());
        }
        return false;
    }

    public boolean isRenderBomPlaceholder(InventoryBillOfMaterialItem billOfMaterialsItem) {
        if (billOfMaterialsItem != null) {
            return billOfMaterialsItem.getState().equals(InventoryBillOfMaterialItemStates.placeholder.getValue());
        }
        return false;
    }

    public boolean isRenderBomExisting(InventoryBillOfMaterialItem billOfMaterialsItem) {
        if (billOfMaterialsItem != null) {
            return billOfMaterialsItem.getState().equals(InventoryBillOfMaterialItemStates.existingItem.getValue());
        }
        return false;
    }

    public boolean isRenderBomEdit(InventoryBillOfMaterialItem billOfMaterialsItem) {
        if (billOfMaterialsItem != null) {
            return billOfMaterialsItem.getState().equals(InventoryBillOfMaterialItemStates.newItem.getValue());
        }
        return false;
    }

    public boolean isRenderItemBom(ItemDomainInventoryBase item) {
        return item.getInventoryDomainBillOfMaterialList() != null
                && item.getInventoryDomainBillOfMaterialList().isEmpty() == false;
    }

    /**
     * Show full bill of materials based on top level item being added.
     *
     * @return
     */
    public boolean isRenderCurrentItemFullBOM() {
        if (getCurrent() == null) {
            return false;
        }
        return isRenderItemBom(getCurrent());
    }

    public void createOptionalBillOfMaterialsPart(InventoryBillOfMaterialItem bomItem) {
        bomItem.setState(InventoryBillOfMaterialItemStates.placeholder.getValue());
        currentItemBOMTreeHasOptionalItems = null;
    }

    public void removeOptionalBillOfMaterialsPart(InventoryBillOfMaterialItem bomItem) {
        // Removal could only happen for optional items 
        if (bomItem.isOptional()) {
            bomItem.setState(InventoryBillOfMaterialItemStates.unspecifiedOptional.getValue());
            if (bomItem.getInventoryItemElement() != null) {
                ItemElement inventoryItemElement = bomItem.getInventoryItemElement();
                ItemDomainInventoryBase current = getCurrent();
                current.getFullItemElementList().remove(inventoryItemElement);
                ItemElementController.getInstance().destroy(inventoryItemElement);
            }
            clearSelectedOptionalElementsIfNeeded();
            currentItemBOMTreeHasOptionalItems = null;
        }
    }

    public void toggleShowOptionalItems() {
        showOptionalPartsInBom = !showOptionalPartsInBom;
        clearSelectedOptionalElementsIfNeeded();
    }

    public Boolean getCurrentItemBOMTreeHasOptionalItems() {
        // TODO add support for optional elements in sub assamblies 
        ItemDomainInventoryBase current = getCurrent();
        if (current != null && currentItemBOMTreeHasOptionalItems == null) {
            currentItemBOMTreeHasOptionalItems = itemHasOptionalsInBOM(current);
            if (!currentItemBOMTreeHasOptionalItems) {
                InventoryBillOfMaterialItem containedInBOM = current.getContainedInBOM();
                List<ItemDomainInventoryBase> newItemsToAdd = containedInBOM.getNewItemsToAdd();
                for (ItemDomainInventoryBase item : newItemsToAdd) {
                    currentItemBOMTreeHasOptionalItems = itemHasOptionalsInBOM(item);
                    if (currentItemBOMTreeHasOptionalItems) {
                        return true;
                    }
                }
            }

        }
        return currentItemBOMTreeHasOptionalItems;
    }

    private boolean itemHasOptionalsInBOM(ItemDomainInventoryBase item) {
        List<InventoryBillOfMaterialItem> iBomiList = item.getInventoryDomainBillOfMaterialList();
        for (InventoryBillOfMaterialItem iBomi : iBomiList) {
            if (iBomi.isOptional()) {
                if (iBomi.getState().equals(InventoryBillOfMaterialItemStates.unspecifiedOptional.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void clearSelectedOptionalElementsIfNeeded() {
        if (showOptionalPartsInBom == false && selectedItemBOMTreeNode != null) {
            InventoryBillOfMaterialItem selectedBOM = (InventoryBillOfMaterialItem) selectedItemBOMTreeNode.getData();
            if (selectedBOM.isOptional()
                    && selectedBOM.getState().equals(InventoryBillOfMaterialItemStates.unspecifiedOptional.getValue())) {
                selectedItemBOMTreeNode.setSelected(false);
                selectedItemBOMTreeNode = null;
            }
        }
    }

    public boolean isShowOptionalPartsInBom() {
        return showOptionalPartsInBom;
    }

    public boolean isBOMInventoryItemElementInDB(InventoryBillOfMaterialItem bomItem) {
        ItemElement inventoryItemElement = bomItem.getInventoryItemElement();
        if (inventoryItemElement != null) {
            Integer itemElementId = inventoryItemElement.getId();
            return itemElementId != null;
        }
        return false;
    }

    public void changeBillOfMaterialsState(InventoryBillOfMaterialItem bomItem, String previousState) {
        // Update type of selected tree node. 
        selectedItemBOMTreeNode.setType(bomItem.getState());

        if (!previousState.equals(bomItem.getState())) {
            if (previousState.equals(InventoryBillOfMaterialItemStates.newItem.getValue())) {
                // The current item will not be defined. it has no children.                 
                selectedItemBOMTreeNode.getChildren().clear();

                bomItem.setInventoryItem(null);

                ItemDomainCatalogBase catalogItem = bomItem.getCatalogItem();
                if (catalogItem.getFullItemElementList().size() > 1) {
                    // Assembly may have optionals
                    currentItemBOMTreeHasOptionalItems = null;
                }
            } else if (InventoryBillOfMaterialItemStates.newItem.getValue().equals(bomItem.getState())) {
                ItemElement catalogItemElement = bomItem.getCatalogItemElement();
                ItemDomainCatalog catalogItem = (ItemDomainCatalog) catalogItemElement.getContainedItem();

                ItemDomainInventoryBase newInventoryItem = createEntityInstance();

                newInventoryItem.setDerivedFromItem(catalogItem);
                InventoryBillOfMaterialItem.setBillOfMaterialsListForItem(newInventoryItem, bomItem);

                bomItem.setInventoryItem(newInventoryItem);

                // The tree needs to be updated.
                addNewChildrenToCurrentSelection();

                if (catalogItem.getFullItemElementList().size() > 1) {
                    // Assembly may have optionals
                    currentItemBOMTreeHasOptionalItems = null;
                }
            }
        }
    }

    public boolean isApplyPermissionToAllNewPartsForCurrent() {
        return getControllerUtility().isApplyPermissionToAllNewPartsForItem(getCurrent());
    }

    @Override
    public String getItemElementContainedItemText(ItemElement instanceItemElement) {
        if (instanceItemElement.getContainedItem() == null) {
            if (instanceItemElement.getDerivedFromItemElement().getContainedItem() != null) {
                return "No instance of " + instanceItemElement.getDerivedFromItemElement().getContainedItem().getName() + " defined";
            } else {
                return "Catalog item: " + instanceItemElement.getDerivedFromItemElement().getParentItem().getName() + " has no defined item.";
            }
        }

        ItemDomainInventory containedItem = (ItemDomainInventory) instanceItemElement.getContainedItem();

        return getItemDisplayString(containedItem);
    }

    public boolean isCurrentHasPartsToDisplay() {
        if (getCurrent() == null) {
            return false;
        }
        if (getCurrent().getInventoryDomainBillOfMaterialList() != null) {
            return getCurrent().getInventoryDomainBillOfMaterialList().isEmpty() == false;
        }
        return false;
    }        
}
