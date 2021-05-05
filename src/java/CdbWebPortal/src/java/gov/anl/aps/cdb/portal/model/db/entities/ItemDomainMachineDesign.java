/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.EntityTypeController;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignDeletedItemsController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignInventoryController;
import gov.anl.aps.cdb.portal.controllers.LocatableItemController;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignDeletedControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignInventoryControllerUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import gov.anl.aps.cdb.portal.view.objects.KeyValueObject;
import gov.anl.aps.cdb.portal.view.objects.MachineDesignConnectorCableMapperItem;
import gov.anl.aps.cdb.portal.view.objects.MachineDesignConnectorListObject;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.faces.model.DataModel;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.MACHINE_DESIGN_ID + "")
@Schema(name = "ItemDomainMachineDesign",
        allOf = Item.class
)
public class ItemDomainMachineDesign extends LocatableStatusItem {

    private static final Logger LOGGER = LogManager.getLogger(ItemDomainMachineDesign.class.getName());
    public final static String MD_INTERNAL_STATUS_PROPERTY_TYPE = "Machine Design Status";

    private transient List<ItemElement> combinedItemElementList;
    private transient ItemElement combinedItemElementListParentElement;

    private transient ItemDomainMachineDesign importMdItem = null;
    private transient String importPath = null;
    private transient ItemDomainCatalog importAssignedCatalogItem = null;
    private transient ItemDomainInventory importAssignedInventoryItem = null;
    private transient String importAssignedItemDescription = null;
    private transient ItemDomainLocation importLocationItem = null;
    private transient String importLocationItemString = null;
    private transient String importTemplateAndParameters = null;
    private transient Float importSortOrder = null;
    private transient String moveToTrashErrorMsg = null;
    private transient String moveToTrashWarningMsg = null;

    private transient ItemElement currentHierarchyItemElement;

    private transient ItemElement parentMachineElement;
    
    private transient boolean filterMachineNode = true; 
    private transient boolean skipSetfilterMachineNode = false; 

    // <editor-fold defaultstate="collapsed" desc="Controller variables for current.">        
    private transient List<ItemElementRelationship> relatedMAARCRelationshipsForCurrent = null;
    private transient MachineDesignConnectorCableMapperItem mdccmi;
    private transient List<MachineDesignConnectorListObject> mdConnectorList;
    private transient ItemDomainMachineDesign newMdInventoryItem = null;
    // <editor-fold defaultstate="collapsed" desc="Element edit variables ">
    private transient Item inventoryForElement = null;
    private transient Item catalogForElement = null;
    private transient Item originalForElement = null;
    protected transient DataModel installedInventorySelectionForCurrentElement;
    protected transient DataModel machineDesignTemplatesSelectionList;
    protected transient DataModel topLevelMachineDesignSelectionList;
    private transient List<KeyValueObject> machineDesignNameList = null;
    private transient String machineDesignName = null;
    private transient boolean displayCreateMachineDesignFromTemplateContent = false;
    // </editor-fold>   
    // </editor-fold>   

    @Override
    public Item createInstance() {
        return new ItemDomainMachineDesign();
    }

    @JsonIgnore
    public List<ItemElement> getCombinedItemElementList(ItemElement element) {
        if (combinedItemElementListParentElement != null) {
            if (!element.equals(combinedItemElementListParentElement)) {
                combinedItemElementList = null;
            }
        } else {
            combinedItemElementList = null;
        }

        if (combinedItemElementList == null) {
            combinedItemElementList = new ArrayList<>();
            combinedItemElementListParentElement = element;

            Item containedItem2 = getAssignedItem();
            if (containedItem2 != null) {
                combinedItemElementList.addAll(containedItem2.getItemElementDisplayList());
            }
            combinedItemElementList.addAll(getItemElementDisplayList());
        }

        return combinedItemElementList;
    }

    public ItemElement getCurrentHierarchyItemElement() {
        return currentHierarchyItemElement;
    }

    public void setCurrentHierarchyItemElement(ItemElement currentHierarchyItemElement) {
        this.currentHierarchyItemElement = currentHierarchyItemElement;
    }
    
    @JsonIgnore
    public boolean isFilterMachineNode() {
        return filterMachineNode;
    }

    public void setFilterMachineNode(boolean filterMachineNode) {        
        if (skipSetfilterMachineNode) {
            skipSetfilterMachineNode = false; 
            return;
        }
        this.filterMachineNode = filterMachineNode;
    }
    
    public void updateFilterMachineNode(boolean filterMachineNode) {        
        this.filterMachineNode = filterMachineNode;
        // GUI will try and override the boolean one time during update. 
        skipSetfilterMachineNode = true; 
    }

    @JsonIgnore
    public ItemDomainMachineDesign getParentMachineDesign() {
        ItemElement parentMachineElement = getParentMachineElement();

        if (parentMachineElement != null) {
            return (ItemDomainMachineDesign) parentMachineElement.getParentItem();
        }

        return null;
    }   

    @JsonIgnore
    public ItemElement getParentMachineElement() {
        if (parentMachineElement == null) {
            List<ItemElement> itemElementMemberList = this.getItemElementMemberList();

            if (itemElementMemberList != null) {
                for (ItemElement memberElement : itemElementMemberList) {
                    Item parentItem = memberElement.getParentItem();
                    if (parentItem instanceof ItemDomainMachineDesign) {
                        // Should only be one. 
                        parentMachineElement = memberElement; 
                        return parentMachineElement; 
                    }
                }
            }
        }

        return parentMachineElement;
    }

    @Override
    public ItemController getItemDomainController() {
        if (isItemDeleted(this)) {
            return ItemDomainMachineDesignDeletedItemsController.getInstance();
        }
        if (isItemInventory(this)) {
            return ItemDomainMachineDesignInventoryController.getInstance();
        }
        return ItemDomainMachineDesignController.getInstance();
    }

    @Override
    public ItemControllerUtility getItemControllerUtility() {
        if (isItemDeleted(this)) {
            return new ItemDomainMachineDesignDeletedControllerUtility();
        }
        if (isItemInventory(this)) {
            return new ItemDomainMachineDesignInventoryControllerUtility();
        }
        return new ItemDomainMachineDesignControllerUtility();
    }

    @Override
    public Item getActiveLocation() {
        if (location != null) {
            return location;
        }
        return membershipLocation;
    }

    @Override
    public String getLocationDetails() {
        if (location != null) {
            return locationDetails;
        }
        return super.getLocationDetails();
    }

    public static boolean isItemDeleted(Item item) {
        return isItemEntityType(item, EntityTypeName.deleted.getValue());
    }

    public static boolean isItemInventory(Item item) {
        return isItemEntityType(item, EntityTypeName.inventory.getValue());
    }

    @Override
    public String toString() {
        // Only top level machine design will get the special derived from formatting... DerivedItem - [name]
        if (this.getDerivedFromItem() != null) {
            if (this.getParentMachineDesign() != null) {
                return this.getName();
            }
        }

        return super.toString();
    }

    @JsonIgnore
    public void setImportIsTemplate(Boolean importIsTemplate) {
        if (importIsTemplate) {
            // mark this item as template entity type
            setIsTemplate();
        }
    }

    @JsonIgnore
    public String getImportIsTemplateString() {
        if (isItemTemplate(this)) {
            return "yes";
        } else {
            return "no";
        }
    }

    /**
     * Marks this machine design item as a template EntityType.
     */
    public void setIsTemplate() {
        try {
            List<EntityType> entityTypeList = new ArrayList<>();
            EntityType templateEntity
                    = EntityTypeController.getInstance().
                            findByName(EntityTypeName.template.getValue());
            entityTypeList.add(templateEntity);
            setEntityTypeList(entityTypeList);
        } catch (CdbException ex) {
            String msg = "Exception setting template entity type for: " + getName()
                    + " reason: " + ex.getMessage();
            LOGGER.error("setIsTemplate() " + msg);
        }
    }

    /**
     * Marks this machine design item as a 'deleted' EntityType.
     */
    public void setIsDeleted() {
        try {
            addEntityType(EntityTypeName.deleted.getValue());
        } catch (CdbException ex) {
            String msg = "Exception setting deleted entity type for: " + getName()
                    + " reason: " + ex.getMessage();
            LOGGER.error("setIsDeleted() " + msg);
        }
    }

    /**
     * Removes 'deleted entity type for item.
     */
    public void unsetIsDeleted() {
        removeEntityType(EntityTypeName.deleted.getValue());
    }

    @JsonIgnore
    public ItemDomainMachineDesign getImportMdItem() {
        return importMdItem;
    }

    public void setImportMdItem(ItemDomainMachineDesign item) {
        importMdItem = item;
    }

    @JsonIgnore
    public String getImportContainerString() {
        ItemDomainMachineDesign itemContainer = this.getImportMdItem();
        if (itemContainer != null) {
            return itemContainer.getName();
        } else {
            return "";
        }
    }

    @JsonIgnore
    public String getImportPath() {
        return importPath;
    }

    public void setImportPath(String importPath) {
        this.importPath = importPath;
    }

    public String getAlternateName() {
        return getItemIdentifier1();
    }

    public void setAlternateName(String n) {
        setItemIdentifier1(n);
    }

    public Item getAssignedItem() {
        ItemElement selfElement = getSelfElement();
        return selfElement.getContainedItem2();
    }

    public void setAssignedItem(Item item) {
        ItemElement selfElement = getSelfElement();
        selfElement.setContainedItem2(item);
    }

    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult result = super.search(searchPattern);

        Item assignedItem = getAssignedItem();
        if (assignedItem != null) {
            String assignedItemName = assignedItem.getName();
            result.doesValueContainPattern("Assigned Item Name", assignedItemName, searchPattern);
        }

        return result;
    }

    @Override
    public String getStatusPropertyTypeName() {
        return MD_INTERNAL_STATUS_PROPERTY_TYPE;
    }

    public String getMoveToTrashErrorMsg() {
        return moveToTrashErrorMsg;
    }

    public void setMoveToTrashErrorMsg(String moveToTrashErrorMsg) {
        this.moveToTrashErrorMsg = moveToTrashErrorMsg;
    }

    public String getMoveToTrashWarningMsg() {
        return moveToTrashWarningMsg;
    }

    public void setMoveToTrashWarningMsg(String moveToTrashWarningMsg) {
        this.moveToTrashWarningMsg = moveToTrashWarningMsg;
    }

    public String getMoveToTrashRowStyle() {
        if (moveToTrashErrorMsg != null && !moveToTrashErrorMsg.isBlank()) {
            return "invalidTableData";
        } else if (moveToTrashWarningMsg != null && !moveToTrashWarningMsg.isBlank()) {
            return "warningTableData";
        } else {
            return null;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Import functionality">
    @JsonIgnore
    public Float getImportSortOrder() {
        return importSortOrder;
    }

    public void setImportSortOrder(Float importSortOrder) {
        
        this.importSortOrder = importSortOrder;
        
        // update sort order if item already exists, we set sort order in setImportChildParentRelationship() for new items
        if (this.getId() != null) {
            ItemDomainMachineDesign parentItem = getParentMachineDesign();
            if (parentItem == null) {
                return;
            }
            for (ItemElement parentRelElement : parentItem.getItemElementDisplayList()) {
                if (parentRelElement.getContainedItem1Id().equals(this.getId())) {
                    parentRelElement.setSortOrder(importSortOrder);
                    return;
                }
            }
        }
    }

    @JsonIgnore
    public Float getExportSortOrder() {
        ItemDomainMachineDesign parentItem = getParentMachineDesign();
        if (parentItem == null) {
            return null;
        }
        for (ItemElement parentRelElement : parentItem.getItemElementDisplayList()) {
            if (parentRelElement.getContainedItem1Id().equals(this.getId())) {
                return parentRelElement.getSortOrder();
            }
        }
        return null;
    }
    
    @JsonIgnore
    public ItemDomainCatalog getImportAssignedCatalogItem() {
        return importAssignedCatalogItem;
    }

    public void setImportAssignedCatalogItem(ItemDomainCatalog item) {
        importAssignedCatalogItem = item;
    }

    @JsonIgnore
    public String getImportAssignedCatalogItemString() {
        if (importAssignedCatalogItem != null) {
            return importAssignedCatalogItem.getName();
        } else {
            return "";
        }
    }

    public void setImportAssignedInventoryItem(ItemDomainInventory item) {
        importAssignedInventoryItem = item;
    }

    @JsonIgnore
    public ItemDomainInventory getImportAssignedInventoryItem() {
        return importAssignedInventoryItem;
    }

    @JsonIgnore
    public String getImportAssignedInventoryItemString() {
        if (importAssignedInventoryItem != null) {
            return importAssignedInventoryItem.getName();
        } else {
            return "";
        }
    }

    @JsonIgnore
    public String getImportAssignedItemString() {
        if (importAssignedInventoryItem != null) {
            return importAssignedInventoryItem.getName();
        } else if (importAssignedCatalogItem != null) {
            return importAssignedCatalogItem.getName();
        } else {
            return "";
        }
    }

    @JsonIgnore
    public String getImportAssignedItemDescription() {
        return importAssignedItemDescription;
    }

    public void setImportAssignedItemDescription(String importAssignedItemDescription) {
        this.importAssignedItemDescription = importAssignedItemDescription;
    }

    public void setImportLocationItem(ItemDomainLocation locationItem) {
        if (locationItem != null) {
            LocatableItemController.getInstance().setItemLocationInfo(this);
            LocatableItemController.getInstance().updateLocationForItem(
                    this, locationItem, null);
            importLocationItemString = getLocationString();
            importLocationItem = locationItem;
        }
    }

    @JsonIgnore
    public ItemDomainLocation getImportLocationItem() {
        return importLocationItem;
    }

    @JsonIgnore
    public String getImportLocationItemString() {
        return importLocationItemString;
    }

    public void setImportLocationItemString(String str) {
        importLocationItemString = str;
    }

    @JsonIgnore
    public String getImportTemplateAndParameters() {
        return importTemplateAndParameters;
    }

    public void setImportTemplateAndParameters(String importTemplateAndParameters) {
        this.importTemplateAndParameters = importTemplateAndParameters;
    }

    public void applyImportAssignedItem() {
        if (importAssignedInventoryItem != null) {
            setAssignedItem(importAssignedInventoryItem);
        } else if (importAssignedCatalogItem != null) {
            setAssignedItem(importAssignedCatalogItem);
        }
    }

    /**
     * Establishes parent/child relationship, with this item as child of
     * specified parentItem.
     *
     * @param childItem
     */
    public void setImportChildParentRelationship(
            ItemDomainMachineDesign parentItem,
            Float sortOrder) {

        if (parentItem != null) {
            // create ItemElement for new relationship
            ItemElement itemElement = importCreateItemElementForParent(parentItem, null, null, sortOrder);
            setImportChildParentRelationship(this, parentItem, itemElement);
        }
    }

    private static void setImportChildParentRelationship(
            ItemDomainMachineDesign childItem,
            ItemDomainMachineDesign parentItem,
            ItemElement itemElement) {

        itemElement.setImportChildItem(childItem);
    }

    private static ItemElement importCreateItemElementForParent(
            ItemDomainMachineDesign parentItem,
            UserInfo user,
            UserGroup group,
            Float sortOrder) {

        ItemElement itemElement = new ItemElement();

        ItemDomainMachineDesignControllerUtility controllerUtility;
        controllerUtility = new ItemDomainMachineDesignControllerUtility();

        String elementName
                = controllerUtility.generateUniqueElementNameForItem(parentItem);
        itemElement.setName(elementName);

        itemElement.setImportParentItem(parentItem, sortOrder, user, group);

        return itemElement;
    }

    public static ItemDomainMachineDesign importInstantiateTemplateUnderParent(
            ItemDomainMachineDesign templateItem,
            ItemDomainMachineDesign parentItem,
            UserInfo user,
            UserGroup group) {

        String logMethodName = "instantiateTemplateUnderParent() ";

        if (templateItem == null || parentItem == null) {
            LOGGER.error(logMethodName + "must specify both template and parent items");
            return null;
        }

        ItemElement itemElement = importCreateItemElementForParent(parentItem, user, group, null);

        ItemDomainMachineDesignController controller
                = ItemDomainMachineDesignController.getInstance();

        ItemDomainMachineDesign newItem;
        try {

            newItem = controller.createMachineDesignFromTemplate(itemElement, templateItem, user, group);

            controller.createMachineDesignFromTemplateHierachically(itemElement);

            setImportChildParentRelationship(newItem, parentItem, itemElement);

        } catch (CdbException | CloneNotSupportedException ex) {
            LOGGER.error(logMethodName + "failed to instantiate template "
                    + templateItem.getName() + ": " + ex.toString());
            return null;
        }

        return newItem;
    }
    
    private static void collectHierarchyItems(
            ItemDomainMachineDesign parentItem,
            List<ItemDomainMachineDesign> collectedItems,
            int currentLevel,
            Integer numLevels) {
        
        currentLevel = currentLevel + 1;
        
        List<ItemElement> displayList = parentItem.getItemElementDisplayList();
        for (ItemElement ie : displayList) {
            Item childItem = ie.getContainedItem();
            if (childItem instanceof ItemDomainMachineDesign) {
                collectedItems.add((ItemDomainMachineDesign) childItem);
                if ((numLevels == null) || (currentLevel < numLevels)) {
                    collectHierarchyItems(
                            (ItemDomainMachineDesign) childItem,
                            collectedItems,
                            currentLevel,
                            numLevels);
                }
            }
        }

    }
    
    public static void collectHierarchyItems(
            ItemDomainMachineDesign parentItem,
            List<ItemDomainMachineDesign> collectedItems,
            Integer numLevels) {

        collectedItems.add(parentItem);
        if ((numLevels == null) || (numLevels > 1)) {
            collectHierarchyItems(parentItem, collectedItems, 1, numLevels);
        }
    }

// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Controller variables for current.">        
    @JsonIgnore
    public List<ItemElementRelationship> getRelatedMAARCRelationshipsForCurrent() {
        return relatedMAARCRelationshipsForCurrent;
    }

    public void setRelatedMAARCRelationshipsForCurrent(List<ItemElementRelationship> relatedMAARCRelationshipsForCurrent) {
        this.relatedMAARCRelationshipsForCurrent = relatedMAARCRelationshipsForCurrent;
    }

    @JsonIgnore
    public MachineDesignConnectorCableMapperItem getMdccmi() {
        return mdccmi;
    }

    public void setMdccmi(MachineDesignConnectorCableMapperItem mdccmi) {
        this.mdccmi = mdccmi;
    }

    @JsonIgnore
    public List<MachineDesignConnectorListObject> getMdConnectorList() {
        return mdConnectorList;
    }

    public void setMdConnectorList(List<MachineDesignConnectorListObject> mdConnectorList) {
        this.mdConnectorList = mdConnectorList;
    }

    @JsonIgnore
    public ItemDomainMachineDesign getNewMdInventoryItem() {
        return newMdInventoryItem;
    }

    public void setNewMdInventoryItem(ItemDomainMachineDesign newMdInventoryItem) {
        this.newMdInventoryItem = newMdInventoryItem;
    }

    @JsonIgnore
    public String getMachineDesignName() {
        return machineDesignName;
    }

    public void setMachineDesignName(String machineDesignName) {
        this.machineDesignName = machineDesignName;
    }

    @JsonIgnore
    public List<KeyValueObject> getMachineDesignNameList() {
        return machineDesignNameList;
    }

    public void setMachineDesignNameList(List<KeyValueObject> list) {
        this.machineDesignNameList = list;
    }

    @JsonIgnore
    public Item getInventoryForElement() {
        return inventoryForElement;
    }

    public void setInventoryForElement(Item inventoryForElement) {
        this.inventoryForElement = inventoryForElement;
    }

    @JsonIgnore
    public Item getCatalogForElement() {
        return catalogForElement;
    }

    public void setCatalogForElement(Item catalogForElement) {
        this.catalogForElement = catalogForElement;
    }

    @JsonIgnore
    public Item getOriginalForElement() {
        return originalForElement;
    }

    public void setOriginalForElement(Item originalForElement) {
        this.originalForElement = originalForElement;
    }

    @JsonIgnore
    public boolean isDisplayCreateMachineDesignFromTemplateContent() {
        return displayCreateMachineDesignFromTemplateContent;
    }

    @JsonIgnore
    public DataModel getInstalledInventorySelectionForCurrentElement() {
        return installedInventorySelectionForCurrentElement;
    }

    public void setInstalledInventorySelectionForCurrentElement(DataModel installedInventorySelectionForCurrentElement) {
        this.installedInventorySelectionForCurrentElement = installedInventorySelectionForCurrentElement;
    }

    @JsonIgnore
    public DataModel getMachineDesignTemplatesSelectionList() {
        return machineDesignTemplatesSelectionList;
    }

    public void setMachineDesignTemplatesSelectionList(DataModel machineDesignTemplatesSelectionList) {
        this.machineDesignTemplatesSelectionList = machineDesignTemplatesSelectionList;
    }

    @JsonIgnore
    public DataModel getTopLevelMachineDesignSelectionList() {
        return topLevelMachineDesignSelectionList;
    }

    public void setTopLevelMachineDesignSelectionList(DataModel topLevelMachineDesignSelectionList) {
        this.topLevelMachineDesignSelectionList = topLevelMachineDesignSelectionList;
    }

    // </editor-fold>
}
