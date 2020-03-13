/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

/**
 *
 * @author cdb
 */
public class ItemCoreMetadataPropertyInfo {

    protected String displayName = "";
    protected String propertyName = "";
    
    public ItemCoreMetadataPropertyInfo(String dn, String pn) {
        displayName = dn;
        propertyName = pn;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    
}
