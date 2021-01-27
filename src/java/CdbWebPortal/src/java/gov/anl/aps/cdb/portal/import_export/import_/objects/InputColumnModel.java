/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class InputColumnModel {

    protected int columnIndex;
    protected String name;
    protected String description = null;

    private Map<ImportMode, ColumnModeOptions> columnModeOptionsMap = new HashMap<>();
    
    public InputColumnModel(
            int columnIndex,
            String name,
            String description) {

        this.columnIndex = columnIndex;
        this.name = name;
        this.description = description;
    }

    public InputColumnModel(
            int columnIndex,
            String name,
            String description,
            List<ColumnModeOptions> columnModeOptions) {

        this(columnIndex, name, description);
        for (ColumnModeOptions options : columnModeOptions) {
            addColumnModeOptions(options);
        }
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    
    public void addColumnModeOptions(ColumnModeOptions options) {
        columnModeOptionsMap.put(options.getMode(), options);
    }

    public boolean isUsedForMode(ImportMode mode) {
        return columnModeOptionsMap.containsKey(mode);
    }
    
    public boolean isRequiredForMode(ImportMode mode) {
        if (!columnModeOptionsMap.containsKey(mode)) {
            return false;
        } else {
            return columnModeOptionsMap.get(mode).isRequired();
        }
    }

}
