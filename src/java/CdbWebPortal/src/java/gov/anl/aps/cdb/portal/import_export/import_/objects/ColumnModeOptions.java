/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

/**
 *
 * @author craig
 */
public class ColumnModeOptions {

    private ImportMode mode;
    private boolean required;
    
    public ColumnModeOptions(ImportMode mode, boolean required) {
        this.mode = mode;
        this.required = required;
    }

    public ImportMode getMode() {
        return mode;
    }

    public boolean isRequired() {
        return required;
    }
    
}
