/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects.handlers;

import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author craig
 */
public abstract class OutputHandler extends ColumnHandler {
    
    public OutputHandler() {
    }
    
    public OutputHandler(int firstColumnIndex) {
        super(firstColumnIndex);
    }
    
    public OutputHandler(int firstColumnIndex, int lastColumnIndex) {
        super(firstColumnIndex, lastColumnIndex);
    }
    
    public abstract ValidInfo handleOutput(
            Row row,
            Map<Integer, String> cellValueMap,
            CdbEntity entity);

}
