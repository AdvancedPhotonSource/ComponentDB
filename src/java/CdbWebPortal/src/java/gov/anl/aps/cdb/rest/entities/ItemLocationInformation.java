/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableItem;
import java.util.List;
import org.primefaces.model.TreeNode;

/**
 *
 * @author djarosz
 */
public class ItemLocationInformation {

    private LocatableItem locatableItem;
    private ItemDomainLocation locationItem;
    private Item housingItem; 
    private String locationString;
    private String locationDetails;
    private String housingString; 
    private ItemHierarchy locationSingleNodeHierarchy;
    private ItemHierarchy housingSingleNodeHierarchy; 

    public ItemLocationInformation(LocatableItem locatableItem) {
        this.locatableItem = locatableItem;
        
        List<ItemDomainLocation> cachedLocationHierarchy = locatableItem.getCachedLocationHierarchy();
        if (cachedLocationHierarchy != null && cachedLocationHierarchy.size() > 0) {
            int last = cachedLocationHierarchy.size() - 1; 
            locationItem = cachedLocationHierarchy.get(last); 
        }
        
        List<Item> cachedHousingHierarchy = locatableItem.getCachedHousingHierarchy();
        if (cachedHousingHierarchy != null && cachedHousingHierarchy.size() > 0) {
            int last = cachedHousingHierarchy.size() - 1; 
            housingItem = cachedHousingHierarchy.get(last); 
        }        
        
        housingString = locatableItem.getHousingString(); 
        locationDetails = locatableItem.getLocationDetails();

        if (locationItem != null) {
            locationString = locatableItem.getLocationString();             
            
            // Set location hierarchy
            TreeNode locationTree = locatableItem.getLocationTree();
            locationSingleNodeHierarchy = ItemHierarchy.createSingleNodeHierarchyFromTreeNode(locationTree);               
        }
        if (housingItem != null) {
            TreeNode housingTree = locatableItem.getHousingTree();
            housingSingleNodeHierarchy = ItemHierarchy.createSingleNodeHierarchyFromTreeNode(housingTree); 
        }
    }

    public LocatableItem getLocatableItem() {
        return locatableItem;
    }

    public void setLocatableItem(LocatableItem locatableItem) {
        this.locatableItem = locatableItem;
    }

    public ItemDomainLocation getLocationItem() {
        return locationItem;
    }

    public void setLocationItem(ItemDomainLocation locationItem) {
        this.locationItem = locationItem;
    }

    public Item getHousingItem() {
        return housingItem;
    }

    public void setHousingItem(Item housingItem) {
        this.housingItem = housingItem;
    }

    public String getHousingString() {
        return housingString;
    }

    public String getLocationString() {
        return locationString;
    }

    public void setLocationString(String locationString) {
        this.locationString = locationString;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }

    public ItemHierarchy getLocationSingleNodeHierarchy() {
        return locationSingleNodeHierarchy;
    }

    public void setLocationSingleNodeHierarchy(ItemHierarchy locationSingleNodeHierarchy) {
        this.locationSingleNodeHierarchy = locationSingleNodeHierarchy;
    }

    public ItemHierarchy getHousingSingleNodeHierarchy() {
        return housingSingleNodeHierarchy;
    }

}
