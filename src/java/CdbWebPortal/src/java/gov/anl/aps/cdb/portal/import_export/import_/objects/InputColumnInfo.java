/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

/**
 *
 * @author craig
 */
public class InputColumnInfo {

    public String name;
    public boolean isRequired;
    public String description;

    public InputColumnInfo(String name, boolean isRequired, String description) {
        this.name = name;
        this.isRequired = isRequired;
        this.description = description;
    }
}
