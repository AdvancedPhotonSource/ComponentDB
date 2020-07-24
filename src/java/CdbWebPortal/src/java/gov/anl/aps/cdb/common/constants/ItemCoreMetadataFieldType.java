/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.common.constants;

/**
 *
 * @author cmcchesney
 */
public enum ItemCoreMetadataFieldType {

    STRING("string"),
    NUMERIC("numeric"),
    URL("url");

    private final String type;

    private ItemCoreMetadataFieldType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static ItemCoreMetadataFieldType fromString(String type) {
        ItemCoreMetadataFieldType fieldType = null;
        switch (type) {
            case "string":
                fieldType = STRING;
                break;
            case "numeric":
                fieldType = NUMERIC;
                break;
            case "url":
                fieldType = URL;
                break;
        }
        return fieldType;
    }
}