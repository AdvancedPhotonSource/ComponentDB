/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidRequest;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.db.beans.ItemElementRelationshipFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementHistory;
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
import gov.anl.aps.cdb.portal.view.objects.LocationHistoryObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final String REL_PATH_MULTI_EDIT_LOCATION_INPUT = "../../locatableItem/private/applyValuesTo/locationInput.xhtml";

    private static final Logger logger = LogManager.getLogger(LocatableItemController.class.getName());

    private LocatableItem lastInventoryItemRequestedLocationMenuModel = null;

    private ItemDomainLocationController itemDomainLocationController;

    List<ItemHierarchyCache> locationItemHierarchyCaches = null;

    @EJB
    private ItemElementRelationshipFacade itemElementRelationshipFacade;

    @EJB
    private ItemFacade itemFacade;

    @EJB
    RelationshipTypeFacade relationshipTypeFacade;

    private static LocatableItemController apiInstance;
    protected boolean apiMode = false;
    protected UserInfo apiUser;

    public LocatableItemController() {
    }

    public LocatableItemController(ItemElementRelationshipFacade itemElementRelationshipFacade, ItemFacade itemFacade, RelationshipTypeFacade relationshipTypeFacade) {        
        this.itemElementRelationshipFacade = itemElementRelationshipFacade;
        this.itemFacade = itemFacade;
        this.relationshipTypeFacade = relationshipTypeFacade; 
    }

    public static synchronized LocatableItemController getApiInstance() {
        if (apiInstance == null) {
            apiInstance = new LocatableItemController();
            apiInstance.apiMode = true;
            apiInstance.loadEJBResourcesManually();

        }
        return apiInstance;
    }

    protected void loadEJBResourcesManually() {
        itemElementRelationshipFacade = ItemElementRelationshipFacade.getInstance();
        itemFacade = itemFacade.getInstance();
        relationshipTypeFacade = RelationshipTypeFacade.getInstance();
    }

    public static LocatableItemController getInstance() {
        if (SessionUtility.runningFaces()) {
            return (LocatableItemController) SessionUtility.findBean(controllerNamed);
        } else {
            return getApiInstance();
        }
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
        if (item instanceof LocatableItem) {
            LocatableItem locatableItem = (LocatableItem) item;
            return getLocationStringForItem(locatableItem);
        }
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

    // Just to process non-locatable items from gui
    public String getLocationRelationshipDetails(Item item) {
        return "";
    }

    public String getLocationRelationshipDetails(LocatableItem locatableItem) {
        if (locatableItem == null) {
            return null;
        }
        if (locatableItem.getLocationDetails() == null) {
            setItemLocationInfo(locatableItem);
        }

        return locatableItem.getLocationDetails();
    }

    /**
     * Uses a cache to quickly load location information for an item
     *
     * @param cache Attainable by running
     * itemElementRelationshipFacade.findItemElementRelationshipsByTypeAndItemDomain
     * @param locatableItem
     */
    public void loadCachedLocationStringForItem(List<ItemElementRelationship> cache, LocatableItem locatableItem) {
        Integer itemId = locatableItem.getId();

        for (int i = 0; i < cache.size(); i++) {
            ItemElementRelationship ier = cache.get(i);

            ItemElement firstItemElement = ier.getFirstItemElement();
            Item parentItem = firstItemElement.getParentItem();
            Integer relationshipItemId = parentItem.getId();

            if (relationshipItemId.equals(itemId)) {
                loadItemLocationInfo(ier, locatableItem, false, true);
                cache.remove(i);
                return;
            }
        }
        loadItemLocationInfo(null, locatableItem, false, true);

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
            ItemDomainLocation itemLocationItem = locatableItem.getLocationItem();

            if (itemLocationItem == null) {
                ItemElementRelationship itemElementRelationship;
                itemElementRelationship = getItemLocationRelationship(locatableItem);

                loadItemLocationInfo(itemElementRelationship, locatableItem, loadLocationTreeForItem, loadLocationHierarchyString);
            }
            locatableItem.setOriginalLocationLoaded(true);
        }
    }

    private void loadItemLocationInfo(
            ItemElementRelationship locationRelationship,
            LocatableItem locatableItem,
            boolean loadLocationTreeForItem,
            boolean loadLocationHierarchyString) {

        if (locationRelationship != null) {
            loadItemLocationInfoFromRelationship(locationRelationship, locatableItem);
        }
        loadItemLocationInfoItemMembership(locatableItem);

        if (loadLocationTreeForItem) {
            updateLocationTreeForItem(locatableItem);
        }
        if (loadLocationHierarchyString) {
            updateLocationStringForItem(locatableItem);
        }
    }

    private void loadItemLocationInfoFromRelationship(ItemElementRelationship locationRelationship, LocatableItem locatableItem) {
        ItemElement locationSelfItemElement = locationRelationship.getSecondItemElement();
        if (locationSelfItemElement != null) {
            ItemDomainLocation locationItem = (ItemDomainLocation) locationSelfItemElement.getParentItem();
            locatableItem.setLocation(locationItem);
        }
        locatableItem.setLocationDetails(locationRelationship.getRelationshipDetails());
    }

    private void loadItemLocationInfoItemMembership(LocatableItem locatableItem) {
        Item parentItem = getParentLocationItem(locatableItem);
        locatableItem.setMembershipLocation(parentItem);
    }

    private Item getParentLocationItem(Item item) {
        if (item.getItemElementMemberList() == null) {
            // For new items it will be null
            return null;
        }
        List<ItemElement> allMembershipList = new ArrayList<>();
        allMembershipList.addAll(item.getItemElementMemberList());
        allMembershipList.addAll(item.getItemElementMemberList2());

        if (allMembershipList.size() > 1) {
            if (SessionUtility.runningFaces()) {
                SessionUtility.addWarningMessage("Error loading location", "More then one membership for item: " + item.getId());
            }
        } else if (allMembershipList.size() == 1) {
            ItemElement element = allMembershipList.get(0);

            return getLocationItemFromElementObject(element, item);
        }
        return null;
    }

    public static Item getLocationItemFromElementObject(CdbEntity itemElementObject, Item locatableItem) {
        if (itemElementObject instanceof ItemElement || itemElementObject instanceof ItemElementHistory) {
            Item parentItem = null;
            Item locationItem = null;

            if (itemElementObject instanceof ItemElement) {
                parentItem = ((ItemElement) itemElementObject).getParentItem();
            } else {
                parentItem = ((ItemElementHistory) itemElementObject).getParentItem();
            }

            if (parentItem != null) {
                locationItem = parentItem;               
            }

            return locationItem;

        } else {
            return null;
        }
    }

    private ItemElementRelationship getItemLocationRelationship(LocatableItem item) {
        if (item.getLocationRelationship() == null) {
            item.setLocationRelationship(findItemLocationRelationship(item));
        }
        return item.getLocationRelationship();
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
            Item activeLocation = item.getActiveLocation();
            if (activeLocation == null) {
                return;
            }
            Domain domain = activeLocation.getDomain();
            String domainName = domain.getName();
            
            if (domain.getId() == ItemDomainName.MACHINE_DESIGN_ID) {
                domainName = "Machine Element";
            }
            

            if (domain.getId() == ItemDomainName.LOCATION_ID) {
                List<Item> locationHierarchyListForItem = getLocationHierarchyListForItem(item);
                if (locationHierarchyListForItem != null) {
                    String locationString = ItemUtility.generateHierarchyNodeString(locationHierarchyListForItem);
                    item.setLocationString(locationString);
                }
            } else {
                String locationString = "Child of " + domainName + ": " + activeLocation.toString();
                item.setLocationString(locationString);
            }
        }
    }

    /**
     * Using the current location set in item, generate a location tree.
     *
     * @param item
     */
    public void updateLocationTreeForItem(LocatableItem item) {
        List<Item> hiearchyList = getLocationHierarchyListForItem(item);
        if (hiearchyList != null) {
            List<Item> revList = new ArrayList<>();
            revList.addAll(hiearchyList);
            Collections.reverse(revList);
            
            TreeNode treeBranch = ItemUtility.generateHierarchyNodeTreeBranch(revList);
            prepLocationTreeForView(treeBranch);
            item.setLocationTree(treeBranch);
        }
    }

    public TreeNode getLocationTreeForLocationHistoryObject(LocationHistoryObject locationHistoryObject) {
        TreeNode locationTree = locationHistoryObject.getLocationTree();
        if (locationTree == null) {
            Item locationItem = locationHistoryObject.getLocationItem();
            locationTree = generateLocationTreeForLocationItem(locationItem);
            locationHistoryObject.setLocationTree(locationTree);
        }
        return locationTree;
    }

    public TreeNode generateLocationTreeForLocationItem(Item location) {
        if (location != null) {
            List<Item> hierarchyList = generateLocationHierarchyList(location);
            TreeNode treeRoot = ItemUtility.generateHierarchyNodeTreeBranch(hierarchyList);
            prepLocationTreeForView(treeRoot);

            return treeRoot;
        }
        return null;
    }

    private void prepLocationTreeForView(TreeNode root) {
        // Allow for quick view full location. 
        ItemUtility.setExpandedSelectedOnAllChildren(root, true, false);
        if (root.getChildCount() > 0) {
            root.getChildren().get(0).setExpanded(false);
        }
    }

    public List<Item> getLocationHierarchyListForItem(LocatableItem item) {
        List<Item> cachedLocationHierarchy = item.getCachedLocationHierarchy();
        if (cachedLocationHierarchy != null) {
            return cachedLocationHierarchy;
        } else {
            Item location = item.getActiveLocation();
            if (location != null) {
                List<Item> hierarchyList = generateLocationHierarchyList(location);

                hierarchyList = appendMachineDesignContext(item, hierarchyList);

                item.setCachedLocationHierarchy(hierarchyList);

                return hierarchyList;
            }
        }

        return null;

    }

    public List<Item> appendMachineDesignContext(LocatableItem item, List<Item> hierarchyList) {
        if (item instanceof ItemDomainMachineDesign) {
            LocatableItem membershipLocation = (LocatableItem) item.getMembershipLocation();
            
            if (membershipLocation != null) {
                Item activeLocation = item.getActiveLocation();
                
                if (activeLocation.equals(membershipLocation) == false) {
                    List<Item> finalHierarchy = generateLocationHierarchyList(membershipLocation);

                    for (int i = finalHierarchy.size() - 1; i >= 0; i--) {
                        Item memberItem = finalHierarchy.get(i);
                        if (memberItem instanceof ItemDomainLocation) {
                            int hieararchyLastSize = hierarchyList.size();
                            for (int j = hieararchyLastSize - 2; j >= 0; j--) {
                                Item hierarchyItem = hierarchyList.get(j);
                                if (hierarchyItem.equals(memberItem)) {
                                    finalHierarchy.addAll(hierarchyList.subList(j + 1, hieararchyLastSize));
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    hierarchyList = finalHierarchy;
                }

                hierarchyList = appendMachineDesignContext(membershipLocation, hierarchyList);
            }
        }

        return hierarchyList;
    }

    public List<Item> generateLocationHierarchyList(Item lowestLocationItem) {
        if (lowestLocationItem != null) {
            List<Item> itemHerarchyList = new ArrayList<>();
            Item currentLowestItem = lowestLocationItem;

            while (currentLowestItem != null) {
                itemHerarchyList.add(0, currentLowestItem);
                if (currentLowestItem instanceof LocatableItem) {
                    setItemLocationInfo((LocatableItem) currentLowestItem, false, false);
                    currentLowestItem = ((LocatableItem) currentLowestItem).getActiveLocation();
                } else {
                    // Location item 
                    currentLowestItem = getParentLocationItem(currentLowestItem);
                }
            }

            return itemHerarchyList;
        }
        return null;
    }

    public ItemDomainLocation getLocation(LocatableItem inventoryItem) {
        if (inventoryItem.getOriginalLocationLoaded() == false) {
            setItemLocationInfo(inventoryItem);
        }
        return inventoryItem.getLocationItem();
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
            Item location = null;

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
                List<Item> locationList = generateLocationHierarchyList(location);
                List<ItemHierarchyCache> currentItemHierarchyCache = getLocationItemHierarchyCaches();
                for (Item locationItem : locationList) {
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
                Item lowestLocation = item.getActiveLocation();

                List<Item> activeItemList = (List<Item>) (List<?>) generateLocationHierarchyList(lowestLocation);

                // TODO reset hierarchy caches on location change. 
                itemLocationMenuModel = ItemElementUtility.generateItemSelectionMenuModel(
                        hierarchyCaches,
                        locationString,
                        controllerNamed,
                        "updateLocationForLastRequestedMenuModel",
                        activeItemList,
                        "Clear Location",
                        updateTarget,
                        updateTarget);
                item.setLocationMenuModel(itemLocationMenuModel);
            }
            return item.getLocationMenuModel();
        }
        return null;
    }

    public void updateLocationForLastRequestedMenuModel(Item item) {
        if (lastInventoryItemRequestedLocationMenuModel != null) {
            if (item instanceof ItemDomainLocation) {
                ItemDomainLocation locationItem = (ItemDomainLocation) item;
                updateLocationForItem(lastInventoryItemRequestedLocationMenuModel, locationItem, null);
            } else if (item == null) {
                updateLocationForItem(lastInventoryItemRequestedLocationMenuModel, null, null);
            }
        } else {
            SessionUtility.addErrorMessage("Error", "No current item.");
        }
    }

    public void updateLocationForItem(LocatableItem item, ItemDomainLocation locationItem, String onSuccess) {
        if (item.equals(locationItem)) {
            SessionUtility.addErrorMessage("Error", "Cannot use the same location as this item.");
            return;
        }

        Boolean originalLocationLoaded = item.getOriginalLocationLoaded();
        item.resetLocationVariables();
        item.setOriginalLocationLoaded(originalLocationLoaded);

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
        ItemElementRelationship itemLocationRelationship = getItemLocationRelationship(item);

        if (itemLocationRelationship != null) {
            return itemLocationRelationship.getItemElementRelationshipHistoryList();
        }

        return null;
    }

    public List<LocationHistoryObject> getLocationHistoryObjectList(LocatableItem item) {
        List<LocationHistoryObject> historyObjectList = new ArrayList<>();

        List<ItemElementRelationshipHistory> historyList = getItemLocationRelationshipHistory(item);
        if (historyList != null) {
            for (ItemElementRelationshipHistory elementRelationshipHistory : historyList) {
                LocationHistoryObject historyObject = new LocationHistoryObject(elementRelationshipHistory);
                historyObjectList.add(historyObject);
            }
        }
        List<ItemElementHistory> historyMemberList = new ArrayList();
        historyMemberList.addAll(item.getHistoryMemberList());
        historyMemberList.addAll(item.getHistoryMemberList2());

        for (ItemElementHistory itemElementHistory : historyMemberList) {
            Item locationItem = getLocationItemFromElementObject(itemElementHistory, item);
            LocationHistoryObject historyObject = new LocationHistoryObject(itemElementHistory, locationItem);
            historyObjectList.add(historyObject);
        }

        Collections.sort(historyObjectList);

        return historyObjectList;
    }

    public static String generateLocationDetailsFromItem(Item item) {
        String partOf = "assembly";
        Domain domain = item.getDomain();

        if (domain.getId() == ItemDomainName.MACHINE_DESIGN_ID) {
            return "Child of Machine Element";
        }
        else if (domain.getId() != ItemDomainName.INVENTORY_ID) {
            partOf = domain.getName();
        }

        return "Assigned to " + partOf;
    }

    public boolean locationEditable(Item item) {
        if (item instanceof LocatableItem) {
            if (item instanceof ItemDomainMachineDesign) {
                return true;
            }
            LocatableItem locatableItem = (LocatableItem) item;
            // Loads the location if needed. 
            getLocation(locatableItem);

            Item activeLocation = locatableItem.getActiveLocation();
            if (activeLocation != null) {
                return activeLocation.getDomain().getId() == ItemDomainName.LOCATION_ID;
            }
            return true;
        }
        return false;
    }   
    
    protected void updateItemLocation(LocatableItem item) throws InvalidRequest {
        UserInfo user = SessionUtility.getUser();
        updateItemLocation(item, user);
    }

    public void updateItemLocation(LocatableItem item, UserInfo updateUser) throws InvalidRequest {
        if (item.getOriginalLocationLoaded() == false) {
            // Location was never loaded. 
            return;
        }

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
                && (item.getLocationItem() != null || (item.getLocationDetails() != null && !item.getLocationDetails().isEmpty())));

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

            locationDifferentOnCurrentItem = ((!Objects.equals(existingItem.getLocationItem(), item.getLocationItem())
                    || !Objects.equals(existingLocationDetails, newLocationDetails)));
        }

        if (newItemWithNewLocation || locationDifferentOnCurrentItem) {
            // Verify that the location can be changed. 
            if ((existingItem != null) && (locationEditable(existingItem)) == false) {
                Item location = existingItem.getActiveLocation();
                throw new InvalidRequest("Error updating location. The item is currently part of a " + location.getDomain().getName() + " item.");
            }

            if (item.getLocationItem() != null) {
                logger.debug("Updating location for Item " + item.toString()
                        + " to: " + item.getLocationItem().getName());
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
            
            // iterate through list to find location relationship, in case pointer has been reloaded
            ItemElementRelationship ier = null;
            String relationshipTypeName = ItemElementRelationshipTypeNames.itemLocation.getValue();
            for (ItemElementRelationship rel : itemElementRelationshipList) {
                if (rel.getRelationshipType().getName().equals(relationshipTypeName)) {
                    ier = rel;
                    break;
                }
            }

            if (ier != null) {
                if (item.getLocationItem() != null && item.getLocationItem().getSelfElement() != null) {
                    ier.setSecondItemElement(item.getLocationItem().getSelfElement());
                } else {
                    // No location is set. 
                    ier.setSecondItemElement(null);
                }
                ier.setRelationshipDetails(item.getLocationDetails());

                // Add Item Element relationship history record. 
                ItemElementRelationshipHistory ierh;               

                ierh = ItemElementRelationshipUtility.createItemElementHistoryRecord(
                        ier, updateUser, new Date());
                List<ItemElementRelationshipHistory> ierhList;
                ierhList = item.getSelfElement().getItemElementRelationshipHistoryList();
                if (ierhList == null) {
                    ierhList = new ArrayList<>();
                    item.getSelfElement().setItemElementRelationshipHistoryList(ierhList);
                }

                ierhList.add(ierh);
                
            } else {
                logger.error("updateItemLocation(): item location relationship unexpectedly null for item: " + item.toString());
            }
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

    public static String getRelPathMultiEditLocationInput() {
        return REL_PATH_MULTI_EDIT_LOCATION_INPUT;
    }

}
