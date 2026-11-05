/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.jsf.handlers.MarkdownPropertyTypeHandler;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/** Scalar snapshot of a property value. */
public final class CdbPropertySnapshot {

    private final String propertyValueId;
    private final String propertyTypeId;
    private final String propertyTypeName;
    private final String tag;
    private final String value;
    private final String text;
    private final String units;
    private final String description;
    private final Map<String, String> metadata;
    private final boolean longText;

    public CdbPropertySnapshot(PropertyValue propertyValue, String text) {
        Object id = propertyValue.getId();
        propertyValueId = id == null ? null : id.toString();
        PropertyType propertyType = propertyValue.getPropertyType();
        propertyTypeId = propertyType == null || propertyType.getId() == null
                ? null : propertyType.getId().toString();
        propertyTypeName = propertyType == null ? null : propertyType.getName();
        tag = propertyValue.getTag();
        value = propertyValue.getValue();
        this.text = text;
        units = propertyValue.getUnits();
        description = propertyValue.getDescription();
        metadata = snapshotMetadata(propertyValue);
        longText = isLongText(propertyValue);
    }

    public static boolean isLongText(PropertyValue propertyValue) {
        if (propertyValue.getText() != null) {
            return true;
        }
        PropertyType propertyType = propertyValue.getPropertyType();
        PropertyTypeHandler handler = propertyType == null
                ? null : propertyType.getPropertyTypeHandler();
        return handler != null && MarkdownPropertyTypeHandler.HANDLER_NAME.equals(handler.getName());
    }

    private static Map<String, String> snapshotMetadata(PropertyValue propertyValue) {
        Map<String, String> result = new TreeMap<>();
        if (propertyValue.getPropertyMetadataList() != null) {
            for (PropertyMetadata propertyMetadata : propertyValue.getPropertyMetadataList()) {
                result.put(propertyMetadata.getMetadataKey(), propertyMetadata.getMetadataValue());
            }
        }
        return Collections.unmodifiableMap(result);
    }

    public String getPropertyValueId() { return propertyValueId; }
    public String getPropertyTypeId() { return propertyTypeId; }
    public String getPropertyTypeName() { return propertyTypeName; }
    public String getTag() { return tag; }
    public String getValue() { return value; }
    public String getText() { return text; }
    public String getUnits() { return units; }
    public String getDescription() { return description; }
    public Map<String, String> getMetadata() { return metadata; }
    public boolean isLongText() { return longText; }
}
