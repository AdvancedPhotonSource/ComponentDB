/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/** Scalar snapshot of a completed item-property change. */
public final class CdbItemPropertyEvent extends CdbMqttEvent {

    public static final int MAX_TEXT_LENGTH = 16 * 1024;

    private final CdbMqttOperation operation;
    private final String itemId;
    private final String itemType;
    private final String itemDescription;
    private final CdbPropertySnapshot oldProperty;
    private final CdbPropertySnapshot newProperty;
    private final String textDiff;
    private final boolean diffTruncated;

    private CdbItemPropertyEvent(CdbMqttTopic topic, Item item,
            PropertyValue oldProperty, PropertyValue newProperty, UserInfo user) {
        super(topic, user);
        if (item == null || (oldProperty == null && newProperty == null)) {
            throw new IllegalArgumentException("item and a property value are required");
        }
        operation = operationFor(topic);
        Object id = item.getId();
        itemId = id == null ? null : id.toString();
        itemType = item.getClass().getSimpleName();
        itemDescription = item.getSystemLogString();

        boolean longText = (oldProperty != null && CdbPropertySnapshot.isLongText(oldProperty))
                || (newProperty != null && CdbPropertySnapshot.isLongText(newProperty));
        String oldText = oldProperty == null ? null : oldProperty.getText();
        String newText = newProperty == null ? null : newProperty.getText();
        if (longText && oldProperty != null && newProperty != null) {
            String diff = createLineDiff(oldText, newText);
            diffTruncated = diff.length() > MAX_TEXT_LENGTH;
            textDiff = truncate(diff);
            this.oldProperty = new CdbPropertySnapshot(oldProperty, null, true);
            this.newProperty = new CdbPropertySnapshot(newProperty, null, true);
        } else {
            String boundedOldText = truncate(oldText);
            String boundedNewText = truncate(newText);
            diffTruncated = length(oldText) > MAX_TEXT_LENGTH || length(newText) > MAX_TEXT_LENGTH;
            textDiff = null;
            this.oldProperty = oldProperty == null ? null
                    : new CdbPropertySnapshot(oldProperty, boundedOldText, false);
            this.newProperty = newProperty == null ? null
                    : new CdbPropertySnapshot(newProperty, boundedNewText, false);
        }
    }

    public static CdbItemPropertyEvent added(Item item, PropertyValue property, UserInfo user) {
        return new CdbItemPropertyEvent(CdbMqttTopic.ITEM_PROPERTY_ADD, item, null, property, user);
    }

    public static CdbItemPropertyEvent updated(Item item, PropertyValue oldProperty,
            PropertyValue newProperty, UserInfo user) {
        return new CdbItemPropertyEvent(CdbMqttTopic.ITEM_PROPERTY_UPDATE,
                item, oldProperty, newProperty, user);
    }

    public static CdbItemPropertyEvent deleted(Item item, PropertyValue property, UserInfo user) {
        return new CdbItemPropertyEvent(CdbMqttTopic.ITEM_PROPERTY_DELETE, item, property, null, user);
    }

    private static CdbMqttOperation operationFor(CdbMqttTopic topic) {
        switch (topic) {
            case ITEM_PROPERTY_ADD: return CdbMqttOperation.ADD;
            case ITEM_PROPERTY_UPDATE: return CdbMqttOperation.UPDATE;
            case ITEM_PROPERTY_DELETE: return CdbMqttOperation.DELETE;
            default: throw new IllegalArgumentException("Invalid property event topic");
        }
    }

    private static String createLineDiff(String oldText, String newText) {
        String[] oldLines = lines(oldText);
        String[] newLines = lines(newText);
        int prefix = 0;
        while (prefix < oldLines.length && prefix < newLines.length
                && oldLines[prefix].equals(newLines[prefix])) {
            prefix++;
        }
        int suffix = 0;
        while (suffix < oldLines.length - prefix && suffix < newLines.length - prefix
                && oldLines[oldLines.length - suffix - 1]
                        .equals(newLines[newLines.length - suffix - 1])) {
            suffix++;
        }

        StringBuilder result = new StringBuilder();
        for (int i = prefix; i < oldLines.length - suffix; i++) {
            result.append('-').append(oldLines[i]).append('\n');
        }
        for (int i = prefix; i < newLines.length - suffix; i++) {
            result.append('+').append(newLines[i]).append('\n');
        }
        return result.toString();
    }

    private static String[] lines(String value) {
        return value == null || value.isEmpty() ? new String[0] : value.split("\\R", -1);
    }

    private static int length(String value) { return value == null ? 0 : value.length(); }
    private static String truncate(String value) {
        return value == null || value.length() <= MAX_TEXT_LENGTH
                ? value : value.substring(0, MAX_TEXT_LENGTH);
    }

    public CdbMqttOperation getOperation() { return operation; }
    public String getItemId() { return itemId; }
    public String getItemType() { return itemType; }
    public String getItemDescription() { return itemDescription; }
    public CdbPropertySnapshot getOldProperty() { return oldProperty; }
    public CdbPropertySnapshot getNewProperty() { return newProperty; }
    public String getTextDiff() { return textDiff; }
    public boolean isDiffTruncated() { return diffTruncated; }
}
