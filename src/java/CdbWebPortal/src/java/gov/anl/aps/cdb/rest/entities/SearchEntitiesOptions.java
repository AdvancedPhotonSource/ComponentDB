/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

/**
 *
 * @author darek
 */
public class SearchEntitiesOptions {
    
    private String searchText; 
    
    private final boolean includeCatalog;
    private final boolean includeInventory;
    private final boolean includeCableCatalog;
    private final boolean includeCableInventory;
    private final boolean includeCableDesign;
    private final boolean includeMachineDesign;
    private final boolean includeItemLocation;
    private final boolean includeMAARC; 
    private final boolean includeItemElement;
    private final boolean includeItemType;
    private final boolean includeItemCategoy;
    private final boolean includePropertyType;
    private final boolean includePropertyTypeCategory;
    private final boolean includeSource;
    private final boolean includeUser;
    private final boolean includeUserGroup;

    public SearchEntitiesOptions() {
        this.includeUserGroup = false;
        this.includeUser = false;
        this.includeSource = false;
        this.includePropertyTypeCategory = false;
        this.includePropertyType = false;
        this.includeItemCategoy = false;
        this.includeItemType = false;
        this.includeItemElement = false;
        this.includeMAARC = false;
        this.includeItemLocation = false;
        this.includeMachineDesign = false;
        this.includeCableDesign = false;
        this.includeCableInventory = false;
        this.includeCableCatalog = false;
        this.includeInventory = false;
        this.includeCatalog = false;
    }

    public String getSearchText() {
        return searchText;
    }        

    public boolean isIncludeCatalog() {
        return includeCatalog;
    }

    public boolean isIncludeInventory() {
        return includeInventory;
    }

    public boolean isIncludeCableCatalog() {
        return includeCableCatalog;
    }

    public boolean isIncludeCableInventory() {
        return includeCableInventory;
    }

    public boolean isIncludeCableDesign() {
        return includeCableDesign;
    }

    public boolean isIncludeMachineDesign() {
        return includeMachineDesign;
    }

    public boolean isIncludeItemLocation() {
        return includeItemLocation;
    }

    public boolean isIncludeMAARC() {
        return includeMAARC;
    }

    public boolean isIncludeItemElement() {
        return includeItemElement;
    }

    public boolean isIncludeItemType() {
        return includeItemType;
    }

    public boolean isIncludeItemCategoy() {
        return includeItemCategoy;
    }

    public boolean isIncludePropertyType() {
        return includePropertyType;
    }

    public boolean isIncludePropertyTypeCategory() {
        return includePropertyTypeCategory;
    }

    public boolean isIncludeSource() {
        return includeSource;
    }

    public boolean isIncludeUser() {
        return includeUser;
    }

    public boolean isIncludeUserGroup() {
        return includeUserGroup;
    }
    
}
