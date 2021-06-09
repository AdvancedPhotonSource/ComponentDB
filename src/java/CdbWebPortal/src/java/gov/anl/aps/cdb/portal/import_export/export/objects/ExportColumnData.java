/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects;

import java.util.List;

/**
 *
 * @author craig
 */
public class ExportColumnData {
    
    private String columnName;
    private String description;
    private List<String> columnValues;
    
    public ExportColumnData(String columnName, String description, List<String> columnData) {
        this.columnName = columnName;
        this.description = description;
        this.columnValues = columnData;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getColumnValues() {
        return columnValues;
    }

    public void setColumnValues(List<String> columnValues) {
        this.columnValues = columnValues;
    }
    
    public void add(String value) {
        this.columnValues.add(value);
    }
    
}
