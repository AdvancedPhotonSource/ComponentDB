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
import gov.anl.aps.cdb.portal.controllers.LocatableItemController;
import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import java.util.ArrayList;
import java.util.List;
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
public class ItemDomainMachineDesign extends LocatableItem {   
    
    private static final Logger LOGGER = LogManager.getLogger(ItemDomainMachineDesign.class.getName());

    private transient List<ItemElement> combinedItemElementList; 
    private transient ItemElement combinedItemElementListParentElement; 
    private transient ItemElement currentItemElement; 
    
    private transient ItemDomainMachineDesign importContainerItem = null;
    private transient String importPath = null;
    private transient ItemDomainCatalog importAssignedCatalogItem = null;
    private transient ItemDomainInventory importAssignedInventoryItem = null;
    private transient ItemDomainLocation importLocationItem = null;
    private transient String importLocationItemString = null;

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
            
            Item containedItem2 = combinedItemElementListParentElement.getContainedItem2();
            if (containedItem2 != null) {
                combinedItemElementList.addAll(containedItem2.getItemElementDisplayList());
            }
            combinedItemElementList.addAll(getItemElementDisplayList()); 
        } 
        
        return combinedItemElementList;
    }
    
    /**
     * Machine Design Heirarchy ensures each item is a child of only one item. 
     * Method returns item element at which the current item is referenced. 
     * 
     * @param item
     * @return 
     */
    @JsonIgnore
    public ItemElement getCurrentItemElement() {
        if (currentItemElement == null) {
            List<ItemElement> itemElementMemberList = getItemElementMemberList();
            if (itemElementMemberList.size() > 0) {
                for (ItemElement itemElement : itemElementMemberList) {
                    Item parentItem = itemElement.getParentItem(); 
                    if (ItemDomainMachineDesignController.isItemMachineDesign(parentItem)) {
                        currentItemElement = itemElement; 
                        break; 
                    }
                }
            } else {
                currentItemElement = new ItemElement();
                currentItemElement.setContainedItem(this);
            }
        }
        
        return currentItemElement; 
    }
    
    @JsonIgnore
    public ItemDomainMachineDesign getParentMachineDesign() {
        List<ItemElement> itemElementMemberList = this.getItemElementMemberList();        
        
        if (itemElementMemberList != null) {
            for (ItemElement memberElement : itemElementMemberList) {
                Item parentItem = memberElement.getParentItem();
                if (parentItem instanceof ItemDomainMachineDesign) {
                    // Should only be one. 
                    return (ItemDomainMachineDesign) parentItem;                
                }
            }
        }
       
       return null;
    }

    @Override
    public ItemController getItemDomainController() {
        return ItemDomainMachineDesignController.getInstance();
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

    public void setImportIsTemplate(Boolean importIsTemplate) {
        if (importIsTemplate) {
            // mark this item as template entity type
            setIsTemplate();
        }
    }
    
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
            EntityType templateEntity = 
                    EntityTypeController.getInstance().
                            findByName(EntityTypeName.template.getValue());
            entityTypeList.add(templateEntity);
            setEntityTypeList(entityTypeList);
        } catch (CdbException ex) {
            String msg = "Exception setting template entity type for: " + getName() + 
                    " reason: " + ex.getMessage();
            LOGGER.error("setIsTemplate() " + msg);
        }
    }
    
    public ItemDomainMachineDesign getImportContainerItem() {
        return importContainerItem;
    }

    public void setImportContainerItem(ItemDomainMachineDesign item) {
        importContainerItem = item;
    }

    public String getImportContainerString() {
        ItemDomainMachineDesign itemContainer = this.getImportContainerItem();
        if (itemContainer != null) {
            return itemContainer.getName();
        } else {
            return "";
        }
    }
    
    public String getImportPath() {
        return importPath;
    }

    public void setImportPath(String importPath) {
        this.importPath = importPath;
    }

    public void setProjectValue(ItemProject project) {
        if (project != null) {
            List<ItemProject> projectList = new ArrayList<>();
            projectList.add(project);
            this.setItemProjectList(projectList);
        }
    }
    
    public String getAlternateName() {
        return getItemIdentifier1();
    }

    public void setAlternateName(String n) {
        setItemIdentifier1(n);
    }

    public void setImportAssignedCatalogItem(ItemDomainCatalog item) {
        importAssignedCatalogItem = item;
    }
    
    public ItemDomainCatalog getImportAssignedCatalogItem() {
        return importAssignedCatalogItem;
    }

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
    
    public ItemDomainInventory getImportAssignedInventoryItem() {
        return importAssignedInventoryItem;
    }

    public String getImportAssignedInventoryItemString() {
        if (importAssignedInventoryItem != null) {
            return importAssignedInventoryItem.getName();
        } else {
            return "";
        }
    }

    public void setImportLocationItem(ItemDomainLocation locationItem) {
        importLocationItem = locationItem;
    }
    
    public ItemDomainLocation getImportLocationItem() {
        return importLocationItem;
    }
    
    public String getImportLocationItemString() {
        return importLocationItemString;
    }
    
    /**
     * Applies the import column values to this machine design item. Establishes 
     * parent/child relationship, with this item as child of specified parentItem.
     * Adds assigned catalog/inventory item
     * 
     * @param childItem 
     */
    public void applyImportValues(ItemDomainMachineDesign parentItem, 
            boolean isValidAssignedItem,
            boolean isValidLocation) {
        
        if (getImportLocationItem() != null) {
            // location was specified for item

            if (isValidLocation) {
                // if valid for this item, update it and use hierarchical location
                // string for import location string
                LocatableItemController.getInstance().setItemLocationInfo(this);
                LocatableItemController.getInstance().updateLocationForItem(
                        this, getImportLocationItem(), null);
                importLocationItemString = getLocationString();
                
            } else {
                // if location is not valid for this item, just use item name for
                // import location string
                importLocationItemString = getImportLocationItem().getName();
            }
            
        }
        
        if (parentItem != null) {
            // create ItemElement for new relationship
            EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
            ItemElement itemElement = new ItemElement();
            itemElement.setEntityInfo(entityInfo);
            itemElement.setParentItem(parentItem);
            String elementName
                    = getItemDomainController().
                            generateUniqueElementNameForItem(parentItem);
            itemElement.setName(elementName);

            int elementSize = parentItem.getItemElementDisplayList().size();
            float sortOrder = elementSize;
            itemElement.setSortOrder(sortOrder);

            // set parent-child relationship
            itemElement.setContainedItem(this);

            // for non-template item, add assigned catalog/inventory item (if any)
            if (isValidAssignedItem) {
                if (importAssignedInventoryItem != null) {
                    itemElement.setContainedItem2(importAssignedInventoryItem);
                } else if (importAssignedCatalogItem != null) {
                    itemElement.setContainedItem2(importAssignedCatalogItem);
                }
            }

            // add ItemElement to parent
            parentItem.getFullItemElementList().add(itemElement);
            parentItem.getItemElementDisplayList().add(0, itemElement);

            // add ItemElement to child
            if (this.getItemElementMemberList() == null) {
                this.setItemElementMemberList(new ArrayList<>());
            }
            this.getItemElementMemberList().add(itemElement);
        }
    }
    
}
