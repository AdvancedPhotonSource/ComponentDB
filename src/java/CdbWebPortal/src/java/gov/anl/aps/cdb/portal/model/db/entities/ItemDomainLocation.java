/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainLocationController;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainLocationControllerUtility;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.primefaces.model.menu.DefaultMenuModel;

/**
 *
 * @author djarosz
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.LOCATION_ID + "")
public class ItemDomainLocation extends Item {

    private transient ItemDomainLocation importParentItem = null;
    private transient String importPath = null;
    private transient Float importSortOrder = null;

    private transient ItemElement parentItemElement = null;
    private transient List<Item> locationsLocatedHere = null;
    private transient List<Item> inventoryLocatedHere = null;
    private transient List<Item> machinesLocatedHere = null;

    // <editor-fold defaultstate="collapsed" desc="Controller variables for current.">        
    private transient DefaultMenuModel parentSelectionMenuModel = null;
    // </editor-fold>

    @Override
    public ItemDomainLocationControllerUtility getItemControllerUtility() {
        return new ItemDomainLocationControllerUtility();
    }

    @JsonIgnore
    public ItemElement getParentItemElement() {
        if (parentItemElement == null) {
            List<ItemElement> itemElementMemberList = getItemElementMemberList();
            if (itemElementMemberList.size() == 1) {
                parentItemElement = itemElementMemberList.get(0);
            }
        }
        return parentItemElement;
    }

    public ItemDomainLocation getParentItem() {
        ItemElement parentItemElement = getParentItemElement();
        if (parentItemElement != null) {
            Item parentItem = parentItemElement.getParentItem();
            if (parentItem instanceof ItemDomainLocation) {
                return (ItemDomainLocation) parentItem;
            }
        }
        return null;
    }

    private synchronized void loadItemsLocatedHere() {
        if (inventoryLocatedHere == null) {
            inventoryLocatedHere = new ArrayList<>();
            int INVENTORY_ID = ItemDomainName.INVENTORY_ID;
            machinesLocatedHere = new ArrayList<>();
            int MACHINE_DESIGN_ID = ItemDomainName.MACHINE_DESIGN_ID;

            locationsLocatedHere = new ArrayList<>();

            List<ItemElementRelationship> relationshipList = getItemElementRelationshipList1();
            String itemLocation = ItemElementRelationshipTypeNames.itemLocation.getValue();
            for (ItemElementRelationship rel : relationshipList) {
                RelationshipType relationshipType = rel.getRelationshipType();
                if (relationshipType.getName().equals(itemLocation)) {
                    ItemElement firstItemElement = rel.getFirstItemElement();
                    Item parentItem = firstItemElement.getParentItem();
                    if (parentItem != null) {
                        int domainId = parentItem.getDomain().getId();
                        if (domainId == INVENTORY_ID) {
                            inventoryLocatedHere.add(parentItem);
                        } else if (domainId == MACHINE_DESIGN_ID) {
                            machinesLocatedHere.add(parentItem);  
                        }
                        
                    }
                }
            }
            for (ItemElement element : getItemElementDisplayList()) {
                Item childItem = element.getContainedItem();

                if (childItem != null) {
                    locationsLocatedHere.add(childItem);
                }
            }

        }
    }

    @JsonIgnore
    public List<Item> getLocationsLocatedHere() {
        if (locationsLocatedHere == null) {
            loadItemsLocatedHere();
        }
        return locationsLocatedHere;
    }

    @JsonIgnore
    public List<Item> getInventoryLocatedHere() {
        if (inventoryLocatedHere == null) {
            loadItemsLocatedHere();
        }
        return inventoryLocatedHere;
    }

    @JsonIgnore
    public List<Item> getMachinesLocatedHere() {
        if (machinesLocatedHere == null) {
            loadItemsLocatedHere();
        }
        return machinesLocatedHere;
    }

    @Override
    public Item createInstance() {
        return new ItemDomainLocation();
    }

    @Override
    public ItemController getItemDomainController() {
        return ItemDomainLocationController.getInstance();
    }

    @JsonIgnore
    public ItemDomainLocation getImportParentItem() {
        return importParentItem;
    }

    @JsonIgnore
    public String getImportParentItemString() {
        if (getImportParentItem() != null) {
            return getImportParentItem().getName();
        } else {
            return "";
        }
    }

    public void setImportParentItem(ItemDomainLocation importParentItem) {
        this.importParentItem = importParentItem;
    }

    @JsonIgnore
    public String getImportPath() {
        return importPath;
    }

    public void setImportPath(String importPath) {
        this.importPath = importPath;
    }

    @JsonIgnore
    public Float getImportSortOrder() {
        return importSortOrder;
    }

    public void setImportSortOrder(Float importSortOrder) {
        this.importSortOrder = importSortOrder;
    }

    /**
     * Establishes parent/child relationship, with this item as child of
     * specified parentItem.
     *
     * @param childItem
     */
    @JsonIgnore
    public void setImportChildParentRelationship(
            ItemDomainLocation parentItem,
            String childName,
            Float sortOrder,
            UserInfo user,
            UserGroup group) {

        if (parentItem != null) {
            ItemElement itemElement = new ItemElement();
            itemElement.setName(childName);
            itemElement.setImportParentItem(parentItem, sortOrder, user, group);
            itemElement.setImportChildItem(this);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Controller variables for current.">        
    @JsonIgnore
    public DefaultMenuModel getParentSelectionMenuModel() {
        return parentSelectionMenuModel;
    }

    public void setParentSelectionMenuModel(DefaultMenuModel parentSelectionMenuModel) {
        this.parentSelectionMenuModel = parentSelectionMenuModel;
    }
    // </editor-fold>

}
