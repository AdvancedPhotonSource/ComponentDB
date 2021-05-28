/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects.handlers;

import gov.anl.aps.cdb.portal.import_export.import_.objects.ExportMode;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.util.List;

/**
 *
 * @author craig
 */
public class RefOutputHandler extends SimpleOutputHandler {
    
    public RefOutputHandler(
            String columnName, 
            String description, 
            String domainGetterMethod,
            String domainTransferGetterMethod) {
        
        super(columnName, description, domainGetterMethod, domainTransferGetterMethod);
    }
    
    @Override
    protected String formatCellValue(Object value, ExportMode exportMode) {  
        
        // use id's for export mode, but don't for transfer mode
        boolean useIdValues;
        if (exportMode == ExportMode.TRANSFER) {
            useIdValues = false;
        } else {
            useIdValues = true;
        }
        
        String columnValue = "";
        if (value != null) {
            if (useIdValues) {
                if (value instanceof List) {
                    List<CdbEntity> objList = (List<CdbEntity>) value;
                    boolean isFirstItem = true;
                    for (CdbEntity obj : objList) {
                        if (!isFirstItem) {
                            columnValue = columnValue + ", ";
                        } else {
                            isFirstItem = false;
                        }
                        columnValue = columnValue + obj.getId().toString();
                    }

                } else if (value instanceof CdbEntity) {
                    CdbEntity obj = (CdbEntity) value;
                    columnValue = obj.getId().toString();
                }
            } else {
                columnValue = value.toString();
            }
        }
        
        return columnValue;
    }

}
