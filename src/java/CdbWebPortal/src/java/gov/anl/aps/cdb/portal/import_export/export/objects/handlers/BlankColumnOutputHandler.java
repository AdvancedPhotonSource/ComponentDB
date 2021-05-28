/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects.handlers;

import gov.anl.aps.cdb.portal.import_export.export.objects.ColumnValueResult;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ExportMode;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;

/**
 *
 * Handles a blank column in the export spreadsheet by emitting an empty string
 * as the column value.
 * 
 * @author craig
 */
public class BlankColumnOutputHandler extends SimpleOutputHandler {
    
    public BlankColumnOutputHandler(String columnName, String description) {
        super(columnName, description, null, null);
    }

    @Override
    protected ColumnValueResult getColumnValue(CdbEntity entity, ExportMode exportMode, boolean useIdValues) {
        ValidInfo validInfo = new ValidInfo(true, "");
        return new ColumnValueResult(validInfo, "");
    }
    
}
