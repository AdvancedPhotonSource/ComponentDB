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
    private TreeNode housingTree = null; 
    
    private String locationDetails = null;

    private ItemElementHistory itemElementHistory;
    private ItemElementRelationshipHistory itemElementRelationshipHistory;

    private Item locationItem = null;
    private Item housingItem = null; 

    // API only data
    private ItemHierarchy locationSingleNodeHierarchy;
    private ItemHierarchy housingSingleNodeHierarchy; 

    public LocationHistoryObject(ItemElementHistory itemElementHistory) {
        this.itemElementHistory = itemElementHistory;
        
        Item parentItem = itemElementHistory.getParentItem();
        if (parentItem != null) {
            locationDetails = LocatableItemController.generateLocationDetailsFromItem(parentItem);
        } else {
            locationDetails = "Deleted Item";
            locationDetails += ": " + itemElementHistory.getSnapshotParentName();
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
    
    @JsonIgnore
    public Item getParentItem() {
        if (itemElementHistory != null) {
            return itemElementHistory.getParentItem(); 
        }
        return null; 
    }

    public Item getLocationItem() {
        return locationItem;
    }

    public void setLocationItem(Item locationItem) {
        this.locationItem = locationItem;
    }

    public Item getHousingItem() {
        return housingItem;
    }

    public void setHousingItem(Item housingItem) {
        this.housingItem = housingItem;
    }

    @JsonIgnore
    public TreeNode getLocationTree() {
        return locationTree;
    }

    public void setLocationTree(TreeNode locationTree) {
        this.locationTree = locationTree;
    }

    @JsonIgnore
    public TreeNode getHousingTree() {
        return housingTree;
    }

    public void setHousingTree(TreeNode housingTree) {
        this.housingTree = housingTree;
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

    public ItemHierarchy getHousingSingleNodeHierarchy() {
        if (housingSingleNodeHierarchy == null) {
            TreeNode housingTree = getHousingTree();
            if (housingTree != null && housingTree.getChildCount() > 0) {
                housingSingleNodeHierarchy = ItemHierarchy.createSingleNodeHierarchyFromTreeNode(housingTree); 
            }
        }
        return housingSingleNodeHierarchy;
    }

    @Override
    public int compareTo(LocationHistoryObject o) {
        return o.getEnteredOnDateTime().compareTo(getEnteredOnDateTime());
    }

}
