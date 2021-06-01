/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.objects.CdbObject;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ExportMode;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import java.util.List;
import java.util.Map;

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
    protected String formatCellValue(Object value, ExportMode exportMode) throws CdbException {  
        
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
                if (value instanceof Map) {
                    Map map = (Map) value;
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        columnValue = mapper.writeValueAsString(map);
                    } catch (JsonProcessingException e) {
                        throw new CdbException("Error converting attribute map to json.");
                    }
                    
                } else if (value instanceof Item) {
                    Item item = (Item) value;
                    columnValue = "#" + item.getName();
                    
                } else if (value instanceof CdbObject) {
                    CdbObject obj = (CdbObject) value;
                    columnValue = "#" + obj.getName();
                    
                } else {
                    columnValue = "#" + value.toString();
                }
            }
        }
        
        return columnValue;
    }

}
