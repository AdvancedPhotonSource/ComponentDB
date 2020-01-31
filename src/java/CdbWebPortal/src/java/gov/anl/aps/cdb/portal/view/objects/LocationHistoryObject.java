/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.controllers.LocatableItemController;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementHistory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationshipHistory;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.rest.entities.ItemHierarchy;
import java.util.Date;
import org.primefaces.model.TreeNode;

public class LocationHistoryObject implements Comparable<LocationHistoryObject> {

    private TreeNode locationTree = null;
    private String locationDetails = null;

    private ItemElementHistory itemElementHistory;
    private ItemElementRelationshipHistory itemElementRelationshipHistory;
    
    private Item locationItem = null; 
    
    // API only data
    private ItemHierarchy locationSingleNodeHierarchy;

    public LocationHistoryObject(ItemElementHistory itemElementHistory, Item locationItem) {
        this.itemElementHistory = itemElementHistory;
        this.locationItem = locationItem; 
        if (locationItem != null) {
            locationDetails = LocatableItemController.generateLocationDetailsFromItem(locationItem);                    
        }
    }

    public LocationHistoryObject(ItemElementRelationshipHistory itemElementRelationshipHistory) {
        this.itemElementRelationshipHistory = itemElementRelationshipHistory;
        ItemElement secondItemElement = itemElementRelationshipHistory.getSecondItemElement();
        if (secondItemElement != null) {
            locationItem = secondItemElement.getParentItem();            
        }
        locationDetails = itemElementRelationshipHistory.getRelationshipDetails(); 
    }

    public Item getLocationItem() {
        return locationItem;
    }

    @JsonIgnore
    public TreeNode getLocationTree() {
        return locationTree;
    }

    public void setLocationTree(TreeNode locationTree) {
        this.locationTree = locationTree;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getEnteredOnDateTime() {
        if (itemElementHistory != null) {
            return itemElementHistory.getEnteredOnDateTime();
        } else if (itemElementRelationshipHistory != null) {
            return itemElementRelationshipHistory.getEnteredOnDateTime(); 
        }
        return null; 
    }

    public UserInfo getEnteredByUser() {
        if (itemElementHistory != null) {
            return itemElementHistory.getEnteredByUser(); 
        } else if (itemElementRelationshipHistory != null) {
            return itemElementRelationshipHistory.getEnteredByUser();
        }
        return null; 
    }

    public ItemHierarchy getLocationSingleNodeHierarchy() {
        if (locationSingleNodeHierarchy == null) {
            if (getLocationTree() != null) {
                locationSingleNodeHierarchy = ItemHierarchy.createSingleNodeHierarchyFromTreeNode(getLocationTree());
            } 
        }
        return locationSingleNodeHierarchy;
    }

    @Override
    public int compareTo(LocationHistoryObject o) {
        return o.getEnteredOnDateTime().compareTo(getEnteredOnDateTime());
    }

}
