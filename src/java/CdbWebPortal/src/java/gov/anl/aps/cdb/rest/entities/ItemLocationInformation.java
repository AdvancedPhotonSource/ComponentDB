/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableItem;
import org.primefaces.model.TreeNode;

/**
 *
 * @author djarosz
 */
public class ItemLocationInformation {

    private LocatableItem locatableItem;
    private ItemDomainLocation locationItem;
    private String locationString;
    private String locationDetails;
    private ItemHierarchy locationSingleNodeHierarchy;

    public ItemLocationInformation(LocatableItem locatableItem) {
        this.locatableItem = locatableItem;
        
        locationItem = locatableItem.getLocation();        
        locationDetails = locatableItem.getLocationDetails();

        if (locationItem != null) {
            locationString = locatableItem.getLocationString();             
            
            // Set location hierarchy
            TreeNode locationTree = locatableItem.getLocationTree();
            TreeNode parentNode = locationTree.getChildren().get(0);
            ItemHierarchy parentHierarchy = null;

            while (parentNode != null) {
                Item parent = (Item) parentNode.getData();

                if (locationSingleNodeHierarchy == null) {
                    locationSingleNodeHierarchy = new ItemHierarchy(parent, false);
                    parentHierarchy = locationSingleNodeHierarchy;
                } else {
                    ItemHierarchy child = new ItemHierarchy(parent, false);
                    parentHierarchy.addChildItem(child);
                    parentHierarchy = child;
                }
                if (parentNode.getChildren().size() > 0) {                    
                    parentNode = parentNode.getChildren().get(0);
                } else {
                    parentNode = null;
                }
            }
            
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
