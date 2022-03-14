/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.constants;

public enum ItemDisplayListDataModelScope {
    showAll("All", false),
    showOwned("Owned", true),
    showFavorites("Favorites", true),
    showOwnedPlusFavorites("Owned & Favorites", true),
    showItemsWithPropertyType("Items with Property", false),
    advancedFilter("Advanced Filter", false);

    private String value;
    private boolean settingEntityRequired; 

    private ItemDisplayListDataModelScope(String value, boolean settingEntityRequired) {
        this.value = value;
        this.settingEntityRequired = settingEntityRequired; 
    }

    public String getValue() {
        return value;
    }

    public boolean isSettingEntityRequired() {
        return settingEntityRequired;
    }
};
