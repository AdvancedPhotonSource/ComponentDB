/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.common.exceptions.CdbException;

/**
 *
 * @author djarosz
 */
public abstract class ItemMetadata<MetadataTypeItem extends Item> {

    private MetadataTypeItem item;

    public ItemMetadata(MetadataTypeItem item) {
        this.item = item;
    }

    protected String getCoreMetadataPropertyFieldValue(String key) throws CdbException {
        return item.getCoreMetadataPropertyFieldValue(key);
    }

    protected void setCoreMetadataPropertyFieldValue(String key, String value) throws CdbException {
        item.setCoreMetadataPropertyFieldValue(key, value);
    }

    public PropertyTypeMetadata getCorePropertyTypeMetadata(String key) throws CdbException {
        return item.getCorePropertyTypeMetadata(key);
    }

    public PropertyValue getCoreMetadataPropertyValue() {
        return item.getCoreMetadataPropertyValue();
    }

}
