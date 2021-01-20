/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects.handlers;

import gov.anl.aps.cdb.portal.import_export.export.objects.HandleOutputResult;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.util.List;

/**
 *
 * @author craig
 */
public class RefOutputHandler extends SimpleOutputHandler {
    
    private boolean singleValue = true;
    
    public RefOutputHandler(
            String columnName, 
            String description, 
            String domainGetterMethod, 
            boolean singleValue) {
        
        super(columnName, description, domainGetterMethod);
        this.singleValue = singleValue;
    }
    
    public boolean isSingleValue() {
        return singleValue;
    }
    
    @Override
    public HandleOutputResult handleOutput(List<CdbEntity> entities) {
        return handleOutput(entities, true);
    }
}
