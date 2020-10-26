/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.controllers.LocatableItemController;
import java.util.List;
import org.primefaces.model.TreeNode;
import org.primefaces.model.menu.DefaultMenuModel;

/**
 *
 * @author djarosz
 */
public abstract class LocatableItem extends Item {

    private transient List<Item> cachedLocationHierarchy = null; 
    private transient TreeNode locationTree = null;
    protected transient String locationDetails = null;        
    protected transient Item membershipLocation; 
    protected transient ItemDomainLocation location;
    private transient ItemElementRelationship locationRelationship; 
    private transient String locationString;
    private transient DefaultMenuModel locationMenuModel;
    private transient String importLocationItemString = null;

    // Needed to determine whenever location was removed in edit process. 
    private transient Boolean originalLocationLoaded = false;
    
    public void resetLocationVariables() {
        locationTree = null; 
        locationDetails = null;         
        location = null;
        locationString = null; 
        locationMenuModel = null; 
        originalLocationLoaded = false; 
        locationRelationship = null;
        membershipLocation = null;      
        cachedLocationHierarchy = null; 
    }

    @JsonIgnore
    public TreeNode getLocationTree() {
        return locationTree;
    }

    public void setLocationTree(TreeNode locationTree) {
        this.locationTree = locationTree;
    }

    @JsonIgnore
    public List<Item> getCachedLocationHierarchy() {
        return cachedLocationHierarchy;
    }

    public void setCachedLocationHierarchy(List<Item> cachedLocationHierarchy) {
        this.cachedLocationHierarchy = cachedLocationHierarchy;
    }

    @JsonIgnore
    public DefaultMenuModel getLocationMenuModel() {
        return locationMenuModel;
    }

    public void setLocationMenuModel(DefaultMenuModel locationMenuModel) {
        this.locationMenuModel = locationMenuModel;
    }

    @JsonIgnore
    public Boolean getOriginalLocationLoaded() {
        return originalLocationLoaded;
    }

    public void setOriginalLocationLoaded(Boolean originalLocationLoaded) {
        this.originalLocationLoaded = originalLocationLoaded;
    }

    @JsonIgnore
    public String getLocationDetails() {
        if (membershipLocation != null) {
            return LocatableItemController.generateLocationDetailsFromItem(membershipLocation); 
        }
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        if (membershipLocation != null) {
            return;
        }
        this.locationDetails = locationDetails;
    }  

    @JsonIgnore
    public ItemDomainLocation getLocationItem() {
        return location;
    }
    
    @JsonIgnore
    public void setLocation(ItemDomainLocation location) {
        this.location = location;
    }  
    
    @JsonIgnore
    public Item getActiveLocation() {        
        if (membershipLocation != null) {
            return membershipLocation;            
        }
        return location; 
    }

    @JsonIgnore
    public Item getMembershipLocation() {
        return membershipLocation;
    }

    public void setMembershipLocation(Item membershipLocation) {
        this.membershipLocation = membershipLocation;
    }

    @JsonIgnore
    public ItemElementRelationship getLocationRelationship() {
        return locationRelationship;
    }

    public void setLocationRelationship(ItemElementRelationship locationRelationship) {
        this.locationRelationship = locationRelationship;
    }

    @JsonIgnore
    public String getLocationString() {
        return locationString;
    }

    public void setLocationString(String locationString) {
        this.locationString = locationString;
    }
    
    @Override
    public Item clone() throws CloneNotSupportedException {
        return clone(null, null);
    }

    @Override
    public Item clone(UserInfo ownerUser, UserGroup ownerGroup) throws CloneNotSupportedException {
        
        LocatableItem clonedItem = (LocatableItem) super.clone(ownerUser, ownerGroup);

        clonedItem.setLocationDetails(null);
        clonedItem.setLocation(null);
        
        return clonedItem;
    }

    @JsonIgnore
    public String getImportLocationItemString() {
        return importLocationItemString;
    }
    
    @JsonIgnore
    public void setImportLocationItem(ItemDomainLocation location) {
        LocatableItemController.getInstance().setItemLocationInfo(this);
        LocatableItemController.getInstance().updateLocationForItem(
                this, location, null);
        importLocationItemString = getLocationString();
    }
           
}
