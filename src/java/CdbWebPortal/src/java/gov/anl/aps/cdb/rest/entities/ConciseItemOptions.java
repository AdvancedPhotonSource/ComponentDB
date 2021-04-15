/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

/**
 *
 * @author darek
 */
public class ConciseItemOptions {
    
    private boolean includeDerivedFromItemInfo = false; 
    private boolean includeItemProjectIdList = false; 
    private boolean includeItemTypeIdList = false; 
    private boolean includeItemCategoryIdList = false; 
    private boolean includePrimaryImageForItem = false;

    public ConciseItemOptions() {
    }

    public boolean isIncludeDerivedFromItemInfo() {
        return includeDerivedFromItemInfo;
    }

    public void setIncludeDerivedFromItemInfo(boolean includeDerivedFromItemInfo) {
        this.includeDerivedFromItemInfo = includeDerivedFromItemInfo;
    }

    public boolean isIncludeItemProjectIdList() {
        return includeItemProjectIdList;
    }

    public void setIncludeItemProjectIdList(boolean includeItemProjectIdList) {
        this.includeItemProjectIdList = includeItemProjectIdList;
    }

    public boolean isIncludeItemTypeIdList() {
        return includeItemTypeIdList;
    }

    public void setIncludeItemTypeIdList(boolean includeItemTypeIdList) {
        this.includeItemTypeIdList = includeItemTypeIdList;
    }

    public boolean isIncludeItemCategoryIdList() {
        return includeItemCategoryIdList;
    }

    public void setIncludeItemCategoryIdList(boolean includeItemCategoryIdList) {
        this.includeItemCategoryIdList = includeItemCategoryIdList;
    }

    public boolean isIncludePrimaryImageForItem() {
        return includePrimaryImageForItem;
    }

    public void setIncludePrimaryImageForItem(boolean includePrimaryImageForItem) {
        this.includePrimaryImageForItem = includePrimaryImageForItem;
    }            
    
}
