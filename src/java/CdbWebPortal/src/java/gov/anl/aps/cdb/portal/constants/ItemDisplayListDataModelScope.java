/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.constants;

public enum ItemDisplayListDataModelScope {
    showAll("All", false),
    showOwned("Show Owned", true),
    showFavorites("Show Favorites", true),
    showOwnedPlusFavorites("Show Owned & Favorites", true),
    showItemsWithPropertyType("Show Items with Property", false);

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
