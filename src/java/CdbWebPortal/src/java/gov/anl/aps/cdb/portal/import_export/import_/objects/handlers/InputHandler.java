/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author craig
 */
public abstract class InputHandler extends ColumnHandler {
    
    public InputHandler() {
    }
    
    public InputHandler(int firstColumnIndex) {
        super(firstColumnIndex);
    }
    
    public InputHandler(int firstColumnIndex, int lastColumnIndex) {
        super(firstColumnIndex, lastColumnIndex);
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

}
