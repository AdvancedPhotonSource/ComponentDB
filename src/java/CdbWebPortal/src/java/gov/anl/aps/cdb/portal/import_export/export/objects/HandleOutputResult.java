/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects;

import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import java.util.List;

/**
 *
 * @author craig
 */
public class HandleOutputResult {
    
    private ValidInfo validInfo;
    private List<ExportColumnData> columnData;
    
    public HandleOutputResult(ValidInfo validInfo, List<ExportColumnData> columnData) {
        this.validInfo = validInfo;
        this.columnData = columnData;
    }

    public ValidInfo getValidInfo() {
        return validInfo;
    }

    public void setValidInfo(ValidInfo validInfo) {
        this.validInfo = validInfo;
    }

    public List<ExportColumnData> getColumnData() {
        return columnData;
    }

    public void setColumnData(List<ExportColumnData> columnData) {
        this.columnData = columnData;
    }
    
}
