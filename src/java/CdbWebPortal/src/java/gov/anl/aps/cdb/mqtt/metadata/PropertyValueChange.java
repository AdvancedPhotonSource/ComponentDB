/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt.metadata;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;

/** Pending pre-update state for a property-value change. */
public final class PropertyValueChange {

    public enum Type { ADD, UPDATE, DELETE }

    private final Type type;
    private final PropertyValue oldValue;
    private final PropertyValue newValue;

    private PropertyValueChange(Type type, PropertyValue oldValue, PropertyValue newValue) {
        this.type = type;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public static PropertyValueChange added(PropertyValue value) {
        return new PropertyValueChange(Type.ADD, null, value);
    }

    public static PropertyValueChange updated(PropertyValue oldValue, PropertyValue newValue) {
        return new PropertyValueChange(Type.UPDATE, oldValue, newValue);
    }

    public static PropertyValueChange deleted(PropertyValue value) {
        return new PropertyValueChange(Type.DELETE, value, null);
    }

    public Type getType() { return type; }
    public PropertyValue getOldValue() { return oldValue; }
    public PropertyValue getNewValue() { return newValue; }
}
