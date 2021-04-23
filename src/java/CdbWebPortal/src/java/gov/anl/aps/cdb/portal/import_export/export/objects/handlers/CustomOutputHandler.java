/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects.handlers;

import gov.anl.aps.cdb.portal.import_export.export.objects.ColumnValueResult;
import gov.anl.aps.cdb.portal.import_export.export.objects.HandleOutputResult;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.util.List;

/**
 *
 * @author craig
 */
public class CustomOutputHandler extends SimpleOutputHandler {
    
    private boolean exportIds = false;

    public CustomOutputHandler(
            String columnName, String description, String domainGetterMethod, boolean exportIds) {
        super(columnName, description, domainGetterMethod);
        this.exportIds = exportIds;
    }
    
    @Override
    public HandleOutputResult handleOutput(List<CdbEntity> entities) {
        return handleOutput(entities, exportIds);
    }
    
    @Override
    public ColumnValueResult handleOutput(CdbEntity entity) {
        return handleOutput(entity, exportIds);
    }
    
}
