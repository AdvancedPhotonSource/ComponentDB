/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.portal.import_export.import_.objects.ImportMode;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author craig
 */
public abstract class InputHandler {

    private ColumnSpec columnSpec;
    private int firstColumnIndex = -1;
    private int lastColumnIndex = -1;
    
    public InputHandler() {
    }
    
    public InputHandler(int firstColumnIndex) {
        this.firstColumnIndex = firstColumnIndex;
    }
    
    public InputHandler(int firstColumnIndex, int lastColumnIndex) {
        this(firstColumnIndex);
        this.lastColumnIndex = lastColumnIndex;
    }
    
    public void setColumnSpec(ColumnSpec columnSpec) {
        this.columnSpec = columnSpec;
    }
    
    public int getFirstColumnIndex() {
        return firstColumnIndex;
    }
    
    public void setFirstColumnIndex(int firstIndex) {
        this.firstColumnIndex = firstIndex;
    }

    public int getLastColumnIndex() {
        return lastColumnIndex;
    }
    
    public void setLastColumnIndex(int lastIndex) {
        this.lastColumnIndex = lastIndex;
    }

    public abstract ValidInfo handleInput(
            Row row,
            Map<Integer, String> cellValueMap,
            Map<String, Object> rowMap);

    public ValidInfo updateEntity(
            Map<String, Object> rowMap,
            CdbEntity entity) {
        return new ValidInfo(true, "");
    }

    public boolean isUsedForMode(ImportMode mode) {
        return columnSpec.isUsedForMode(mode);
    }
    
    public boolean isRequiredForMode(ImportMode mode) {
        return columnSpec.isRequiredForMode(mode);
    }

}
