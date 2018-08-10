/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.db.beans.ItemElementRelationshipFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationshipHistory;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableItem;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemElementRelationshipUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemElementUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.ItemHierarchyCache;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;
import org.primefaces.model.menu.DefaultMenuModel;

/**
 *
 * @author djarosz
 */
@Named(LocatableItemController.controllerNamed)
@SessionScoped
public class LocatableItemController implements Serializable {

    public final static String controllerNamed = "locatableItemController";

    private static final Logger logger = Logger.getLogger(LocatableItemController.class.getName());

    private LocatableItem lastInventoryItemRequestedLocationMenuModel = null;

    private ItemDomainLocationController itemDomainLocationController;

    List<ItemHierarchyCache> locationItemHierarchyCaches = null;

    @EJB
    private ItemElementRelationshipFacade itemElementRelationshipFacade;

    @EJB
    private ItemFacade itemFacade;

    @EJB
    RelationshipTypeFacade relationshipTypeFacade;

    public static LocatableItemController getInstance() {
        return (LocatableItemController) SessionUtility.findBean(controllerNamed);
    }

    private ItemDomainLocationController getItemDomainLocationController() {
        if (itemDomainLocationController == null) {
            itemDomainLocationController = ItemDomainLocationController.getInstance();
        }
        return itemDomainLocationController;
    }

    public TreeNode getLocationRelationshipTree(LocatableItem locatableItem) {
        if (locatableItem.getLocationTree() == null) {
            setItemLocationInfo(locatableItem);
        }
        return locatableItem.getLocationTree();
    }

    // Just to process non-locatable items from gui
    public String getLocationStringForItem(Item item) {
        //non-locatable item 
        return "";
    }

    /**
     * Gets a location string for an item and loads it if necessary.
     *
     * @param item
     * @return
     */
    public String getLocationStringForItem(LocatableItem item) {
        if (item != null) {
            if (item.getLocationString() == null) {
                loadLocationStringForItem(item);
                if (item.getLocationString() == null) {
                    // Avoid unecessary checks.
                    item.setLocationString("");
                }
            }
            return item.getLocationString();
        }
        return null;
    }

    /**
     * Load current item location information & set location string.
     *
     * @param locatableItem
     */
    public void loadLocationStringForItem(LocatableItem locatableItem) {
        setItemLocationInfo(locatableItem, false, true);
    }

    public void setItemLocationInfo(LocatableItem locatableItem) {
        setItemLocationInfo(locatableItem, true, true);
    }

    public void setItemLocationInfo(LocatableItem locatableItem, boolean loadLocationTreeForItem, boolean loadLocationHierarchyString) {
        if (locatableItem.getOriginalLocationLoaded() == false) {
            ItemDomainLocation itemLocationItem = locatableItem.getLocation();

            if (itemLocationItem == null) {
                ItemElementRelationship itemElementRelationship;
                itemElementRelationship = findItemLocationRelationship(locatableItem);

                if (itemElementRelationship != null) {
                    ItemElement locationSelfItemElement = itemElementRelationship.getSecondItemElement();
                    if (locationSelfItemElement != null) {
                        ItemDomainLocation locationItem = (ItemDomainLocation) locationSelfItemElement.getParentItem();
                        locatableItem.setLocation(locationItem);
                        if (loadLocationTreeForItem) {
                            updateLocationTreeForItem(locatableItem);
                        }
                        if (loadLocationHierarchyString) {
                            updateLocationStringForItem(locatableItem);
                        }
                    }
                    locatableItem.setLocationDetails(itemElementRelationship.getRelationshipDetails());
                }
            }
            locatableItem.setOriginalLocationLoaded(true);
        }
    }

    private ItemElementRelationship findItemLocationRelationship(LocatableItem item) {
        // Support items that have not yet been saved to db.
        if (item.getSelfElement().getId() != null) {
            try {
                return itemElementRelationshipFacade
                        .findItemElementRelationshipByNameAndItemElementId(ItemElementRelationshipTypeNames.itemLocation.getValue(),
                                item.getSelfElement().getId());
            } catch (CdbException ex) {
                logger.error(ex);
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
            }
        }
        return null;
    }

    /**
     * Using the current location set in item, generate a location string.
     *
     * @param item
     */
    public void updateLocationStringForItem(LocatableItem item) {
        if (item != null) {
            ItemDomainLocation locationItem = item.getLocation();
            item.setLocationString(ItemDomainLocationController.generateLocatonHierarchyString(locationItem));
        }
    }

    /**
     * Using the current location set in item, generate a location tree.
     *
     * @param item
     */
    public void updateLocationTreeForItem(LocatableItem item) {
        if (item != null) {
            ItemDomainLocation location = item.getLocation();
            item.setLocationTree(ItemDomainLocationController.generateLocationNodeTreeBranch(location));
        }
    }

    // Just to process non-locatable items from guiZ
    public String getLocationRelationshipDetails(Item item) {
        return "";
    }

    public String getLocationRelationshipDetails(LocatableItem locatableItem) {
        if (locatableItem.getLocationDetails() == null) {
            setItemLocationInfo(locatableItem);
        }

        return locatableItem.getLocationDetails();
    }

    public ItemDomainLocation getLocation(LocatableItem inventoryItem) {
        if (inventoryItem.getLocation() == null) {
            setItemLocationInfo(inventoryItem);
        }
        return inventoryItem.getLocation();
    }

    public DefaultMenuModel getItemLocataionDefaultMenuModel(LocatableItem item) {
        return getItemLocataionDefaultMenuModel(item, "@form");
    }

    public DefaultMenuModel getItemLocataionDefaultMenuModel(LocatableItem item, String updateTarget) {
        return getItemLocataionDefaultMenuModel(item, updateTarget, getLocationItemHierarchyCaches());
    }

    public DefaultMenuModel getMachineDesignLocationMenuModel(ItemElement editItemElement, String updateTarget) {
        LocatableItem containedItem = (LocatableItem) editItemElement.getContainedItem();

        if (editItemElement.getParentItem() != null) {
            LocatableItem parentItem = (LocatableItem) editItemElement.getParentItem();
            ItemDomainLocation location = null;

            while (parentItem != null) {
                location = getLocation(parentItem);
                if (location != null) {
                    parentItem = null;
                } else {
                    List<ItemElement> itemElementMemberList = parentItem.getItemElementMemberList();
                    if (itemElementMemberList.size() == 1) {
                        // Should only be one for this domain. 
                        ItemElement testElement = itemElementMemberList.get(0);
                        parentItem = (LocatableItem) testElement.getParentItem();
                    } else {
                        parentItem = null;
                    }

                }
            }

            if (location != null) {
                List<ItemDomainLocation> locationList = ItemDomainLocationController.generateLocationHierarchyList(location);
                List<ItemHierarchyCache> currentItemHierarchyCache = getLocationItemHierarchyCaches();
                for (ItemDomainLocation locationItem : locationList) {
                    if (currentItemHierarchyCache != null) {
                        for (ItemHierarchyCache ihc : currentItemHierarchyCache) {
                            if (ihc.getParentItem().equals(locationItem)) {
                                currentItemHierarchyCache = ihc.getChildrenItem();
                            }
                        }
                    } else {
                        break;
                    }
                }
                return getItemLocataionDefaultMenuModel(containedItem, updateTarget, currentItemHierarchyCache);
            }
        }
        return getItemLocataionDefaultMenuModel(containedItem, updateTarget);
    }

    protected DefaultMenuModel getItemLocataionDefaultMenuModel(LocatableItem item,
            String updateTarget,
            List<ItemHierarchyCache> hierarchyCaches) {

        lastInventoryItemRequestedLocationMenuModel = item;
        if (item != null) {
            if (item.getLocationMenuModel() == null) {
                setItemLocationInfo(item, false, true);
                String locationString = item.getLocationString();
                DefaultMenuModel itemLocationMenuModel;

                if (locationString == null || locationString.isEmpty()) {
                    if (hierarchyCaches != null) {
                        locationString = "Select Location";
                    } else {
                        locationString = "No more locations in hierarchy";
                    }
                }
                ItemDomainLocation lowestLocation = item.getLocation();

                List<Item> activeItemList = (List<Item>) (List<?>) ItemDomainLocationController.generateLocationHierarchyList(lowestLocation);

                // TODO reset hierarchy caches on location change. 
                itemLocationMenuModel = ItemElementUtility.generateItemSelectionMenuModel(
                        hierarchyCaches,
                        locationString,
                        controllerNamed,
                        "updateLocationForLastRequestedMenuModel",
                        activeItemList,
                        updateTarget,
                        updateTarget);
                item.setLocationMenuModel(itemLocationMenuModel);
            }
            return item.getLocationMenuModel();
        }
        return null;
    }

    public void updateLocationForLastRequestedMenuModel(Item item) {
        if (item instanceof ItemDomainLocation) {
            ItemDomainLocation locationItem = (ItemDomainLocation) item;
            if (lastInventoryItemRequestedLocationMenuModel != null) {
                updateLocationForItem(lastInventoryItemRequestedLocationMenuModel, locationItem, null);
            } else {
                SessionUtility.addErrorMessage("Error", "No current item.");
            }
        }
    }

    public void updateLocationForItem(LocatableItem item, ItemDomainLocation locationItem, String onSuccess) {
        if (item.equals(locationItem)) {
            SessionUtility.addErrorMessage("Error", "Cannot use the same location as this item.");
            return;
        }

        item.setLocation(locationItem);
        updateLocationTreeForItem(item);
        updateLocationStringForItem(item);
        // To be updated. 
        item.setLocationMenuModel(null);

        if (onSuccess != null) {
            SessionUtility.executeRemoteCommand(onSuccess);
        }
    }

    public List<ItemElementRelationshipHistory> getItemLocationRelationshipHistory(LocatableItem item) {
        String locationRelationshipTypeName = ItemElementRelationshipTypeNames.itemLocation.getValue();

        List<ItemElementRelationship> locationRelationshipList = ItemUtility.getItemRelationshipList(item, locationRelationshipTypeName, true);

        if (locationRelationshipList.size() > 1) {
            SessionUtility.addErrorMessage("Error", "Item is part of two or more locations.");
        } else if (locationRelationshipList.size() == 1) {
            return locationRelationshipList.get(0).getItemElementRelationshipHistoryList();
        }

        return null;
    }

    protected void updateItemLocation(LocatableItem item) {
        // Determie updating of location relationship. 
        LocatableItem existingItem = null;
        ItemElementRelationship itemElementRelationship = null;

        if (item.getId() != null) {
            existingItem = (LocatableItem) itemFacade.find(item.getId());
            // Item is not new
            if (existingItem != null) {
                setItemLocationInfo(existingItem);
                itemElementRelationship = findItemLocationRelationship(item);
            }
        }

        Boolean newItemWithNewLocation = (existingItem == null
                && (item.getLocation() != null || (item.getLocationDetails() != null && !item.getLocationDetails().isEmpty())));

        Boolean locationDifferentOnCurrentItem = false;

        if (existingItem != null) {
            // Empty String should be the same as null for comparison puposes. 
            String existingLocationDetails = existingItem.getLocationDetails();
            String newLocationDetails = item.getLocationDetails();

            if (existingLocationDetails != null && existingLocationDetails.isEmpty()) {
                existingLocationDetails = null;
            }
            if (newLocationDetails != null && newLocationDetails.isEmpty()) {
                newLocationDetails = null;
            }

            locationDifferentOnCurrentItem = ((!Objects.equals(existingItem.getLocation(), item.getLocation())
                    || !Objects.equals(existingLocationDetails, newLocationDetails)));
        }

        if (newItemWithNewLocation || locationDifferentOnCurrentItem) {

            if (item.getLocation() != null) {
                logger.debug("Updating location for Item " + item.toString()
                        + " to: " + item.getLocation().getName());
            } else if (item.getLocationDetails() != null) {
                logger.debug("Updating location details for Item " + item.toString()
                        + " to: " + item.getLocationDetails());
            }

            if (itemElementRelationship == null) {
                itemElementRelationship = new ItemElementRelationship();
                itemElementRelationship.setRelationshipType(getLocationRelationshipType());
                itemElementRelationship.setFirstItemElement(item.getSelfElement());
                List<ItemElementRelationship> itemElementRelationshipList = item.getSelfElement().getItemElementRelationshipList();
                if (itemElementRelationshipList == null) {
                    itemElementRelationshipList = new ArrayList<>();
                    item.getSelfElement().setItemElementRelationshipList(itemElementRelationshipList);
                }
                itemElementRelationshipList.add(itemElementRelationship);
            }

            List<ItemElementRelationship> itemElementRelationshipList = item.getSelfElement().getItemElementRelationshipList();

            Integer locationIndex = itemElementRelationshipList.indexOf(itemElementRelationship);

            if (item.getLocation() != null && item.getLocation().getSelfElement() != null) {
                itemElementRelationshipList.get(locationIndex).setSecondItemElement(item.getLocation().getSelfElement());
            } else {
                // No location is set. 
                itemElementRelationshipList.get(locationIndex).setSecondItemElement(null);
            }
            itemElementRelationshipList.get(locationIndex).setRelationshipDetails(item.getLocationDetails());

            // Add Item Element relationship history record. 
            ItemElementRelationship ier = itemElementRelationshipList.get(locationIndex);
            ItemElementRelationshipHistory ierh;
            ierh = ItemElementRelationshipUtility.createItemElementHistoryRecord(
                    ier, (UserInfo) SessionUtility.getUser(), new Date());
            List<ItemElementRelationshipHistory> ierhList;
            ierhList = item.getSelfElement().getItemElementRelationshipHistoryList();
            if (ierhList == null) {
                ierhList = new ArrayList<>();
                item.getSelfElement().setItemElementRelationshipHistoryList(ierhList);
            }

            ierhList.add(ierh);
        }
    }

    private RelationshipType getLocationRelationshipType() {
        return relationshipTypeFacade.findByName(ItemElementRelationshipTypeNames.itemLocation.getValue());
    }

    public void resetCachedLocationValues() {
        locationItemHierarchyCaches = null;
    }

    public List<ItemHierarchyCache> getLocationItemHierarchyCaches() {
        if (locationItemHierarchyCaches == null) {
            List<Item> baseLocations = (List<Item>) (List<?>) getItemDomainLocationController().getItemsWithoutParents();
            locationItemHierarchyCaches = ItemElementUtility.generateItemHierarchyCacheList(baseLocations);
        }
        return locationItemHierarchyCaches;
    }

}
