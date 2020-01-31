/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableItem;
import org.primefaces.model.TreeNode;

/**
 *
 * @author djarosz
 */
public class ItemLocationInformation {

    private LocatableItem locatableItem;
    private Item locationItem;
    private String locationString;
    private String locationDetails;
    private ItemHierarchy locationSingleNodeHierarchy;

    public ItemLocationInformation(LocatableItem locatableItem) {
        this.locatableItem = locatableItem;
        
        locationItem = locatableItem.getActiveLocation();        
        locationDetails = locatableItem.getLocationDetails();

        if (locationItem != null) {
            locationString = locatableItem.getLocationString();             
            
            // Set location hierarchy
            TreeNode locationTree = locatableItem.getLocationTree();
            locationSingleNodeHierarchy = ItemHierarchy.createSingleNodeHierarchyFromTreeNode(locationTree);             
        }
    }

    public LocatableItem getLocatableItem() {
        return locatableItem;
    }

    public void setLocatableItem(LocatableItem locatableItem) {
        this.locatableItem = locatableItem;
    }

    public Item getLocationItem() {
        return locationItem;
    }

    public void setLocationItem(Item locationItem) {
        this.locationItem = locationItem;
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

}
