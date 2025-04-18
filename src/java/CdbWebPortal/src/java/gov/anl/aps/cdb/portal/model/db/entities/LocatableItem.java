/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.controllers.LocatableItemController;
import gov.anl.aps.cdb.portal.view.objects.LocationHistoryObject;
import java.util.List;
import org.primefaces.model.TreeNode;
import org.primefaces.model.menu.DefaultMenuModel;

/**
 *
 * @author djarosz
 */
public abstract class LocatableItem extends Item {

    private transient List<ItemDomainLocation> cachedLocationHierarchy = null;
    private transient List<Item> cachedHousingHierarchy = null;
    private transient TreeNode locationTree = null;
    private transient TreeNode housingTree = null;
    protected transient String locationDetails = null;
    protected transient Item membershipLocation;
    protected transient ItemDomainLocation location;
    private transient ItemElementRelationship locationRelationship;
    private transient String locationString;
    private transient String housingString;
    private transient DefaultMenuModel locationMenuModel;
    private transient ItemDomainLocation importLocationItem = null;
    private transient String importLocationDetails = null;
    private transient boolean loadedImportLocationItem = false;
    private transient boolean loadedImportLocationDetails = false;
    private transient List<LocationHistoryObject> locationHistoryListObject = null;

    // Needed to determine whenever location was removed in edit process. 
    private transient Boolean originalLocationLoaded = false;
    private transient Boolean membershipLoaded = false;

    public void resetLocationVariables() {
        locationTree = null;
        housingTree = null;
        locationDetails = null;
        location = null;
        locationString = null;
        housingString = null;
        locationMenuModel = null;
        originalLocationLoaded = false;
        locationRelationship = null;
        membershipLocation = null;
        cachedLocationHierarchy = null;
        cachedHousingHierarchy = null;
    }

    @JsonIgnore
    public TreeNode getHousingTree() {
        return housingTree;
    }

    public void setHousingTree(TreeNode housingTree) {
        this.housingTree = housingTree;
    }

    @JsonIgnore
    public TreeNode getLocationTree() {
        return locationTree;
    }

    public void setLocationTree(TreeNode locationTree) {
        this.locationTree = locationTree;
    }

    @JsonIgnore
    public List<Item> getCachedHousingHierarchy() {
        return cachedHousingHierarchy;
    }

    public void setCachedHousingHierarchy(List<Item> cachedHousingHierarchy) {
        this.cachedHousingHierarchy = cachedHousingHierarchy;
    }

    @JsonIgnore
    public List<ItemDomainLocation> getCachedLocationHierarchy() {
        return cachedLocationHierarchy;
    }

    public void setCachedLocationHierarchy(List<ItemDomainLocation> cachedLocationHierarchy) {
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

    @JsonIgnore
    public ItemDomainLocation getActiveInheritedLocation() {
        if (cachedLocationHierarchy != null && !cachedLocationHierarchy.isEmpty()) {
            int last = cachedLocationHierarchy.size() - 1;
            return cachedLocationHierarchy.get(last);
        }
        return null;
    }

    public void setMembershipLocation(Item membershipLocation) {
        this.membershipLocation = membershipLocation;
    }

    public Boolean getMembershipLoaded() {
        return membershipLoaded;
    }

    public void setMembershipLoaded(Boolean membershipLoaded) {
        this.membershipLoaded = membershipLoaded;
    }

    @JsonIgnore
    public ItemElementRelationship getLocationRelationship() {
        return locationRelationship;
    }

    public void setLocationRelationship(ItemElementRelationship locationRelationship) {
        this.locationRelationship = locationRelationship;
    }

    @JsonIgnore
    public String getHousingString() {
        return housingString;
    }

    public void setHousingString(String housingString) {
        this.housingString = housingString;
    }

    @JsonIgnore
    public String getLocationString() {
        return locationString;
    }

    public void setLocationString(String locationString) {
        this.locationString = locationString;
    }

    @Override
    public Item clone(UserInfo ownerUser, UserGroup ownerGroup, boolean cloneProperties, boolean cloneSources, boolean cloneCreateItemElementPlaceholders) throws CloneNotSupportedException {
        LocatableItem clonedItem = (LocatableItem) super.clone(ownerUser, ownerGroup, cloneProperties, cloneSources, cloneCreateItemElementPlaceholders);

        clonedItem.setLocationDetails(null);
        clonedItem.setLocation(null);

        return clonedItem;
    }

    @JsonIgnore
    public void setImportLocationItem(ItemDomainLocation location) {
        LocatableItemController.getInstance().setItemLocationInfo(this);
        LocatableItemController.getInstance().updateLocationForItem(
                this, location, null);
        importLocationItem = location;
    }

    @JsonIgnore
    public ItemDomainLocation getImportLocationItem() {
        if (!loadedImportLocationItem) {
            LocatableItemController.getInstance().setItemLocationInfo(this);
            importLocationItem = getLocationItem();
            loadedImportLocationItem = true;
        }
        return importLocationItem;
    }

    public void setImportLocationDetails(String locationDetails) {
        LocatableItemController.getInstance().setItemLocationInfo(this);
        setLocationDetails(locationDetails);
        importLocationDetails = locationDetails;
    }

    @JsonIgnore
    public String getImportLocationDetails() {
        if (!loadedImportLocationDetails) {
            LocatableItemController.getInstance().setItemLocationInfo(this);
            importLocationDetails = getLocationDetails();
            loadedImportLocationDetails = true;
        }
        return importLocationDetails;
    }

    @JsonIgnore
    public List<LocationHistoryObject> getLocationHistoryListObject() {
        return locationHistoryListObject;
    }

    public void setLocationHistoryListObject(List<LocationHistoryObject> locationHistoryListObject) {
        this.locationHistoryListObject = locationHistoryListObject;
    }

}
