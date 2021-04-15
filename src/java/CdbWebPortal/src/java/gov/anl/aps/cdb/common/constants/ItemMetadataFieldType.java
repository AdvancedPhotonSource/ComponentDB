/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.common.constants;

/**
 *
 * @author cmcchesney
 */
public enum ItemMetadataFieldType {

    STRING("string"),
    NUMERIC("numeric"),
    URL("url");

    private final String type;

    private ItemMetadataFieldType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static ItemMetadataFieldType fromString(String type) {
        ItemMetadataFieldType fieldType = null;
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