/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects.handlers;

import gov.anl.aps.cdb.portal.import_export.export.objects.ColumnValueResult;
import gov.anl.aps.cdb.portal.import_export.export.objects.HandleOutputResult;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ExportMode;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.util.List;

/**
 *
 * @author craig
 */
public abstract class OutputHandler {    
    public abstract HandleOutputResult handleOutput(List<CdbEntity> entities, ExportMode exportMode);
    public abstract ColumnValueResult handleOutput(CdbEntity entity);
}
