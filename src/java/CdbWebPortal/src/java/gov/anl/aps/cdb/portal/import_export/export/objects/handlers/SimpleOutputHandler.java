/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects.handlers;

import gov.anl.aps.cdb.common.objects.CdbObject;
import gov.anl.aps.cdb.portal.import_export.export.objects.ExportColumnData;
import gov.anl.aps.cdb.portal.import_export.export.objects.HandleOutputResult;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Uses reflection to invoke domain object method to retrieve value and writes
 * value to output map.
 * 
 * @author craig
 */
public class SimpleOutputHandler extends SingleColumnOutputHandler {
    
    protected String domainGetterMethod = null;
    
    public SimpleOutputHandler(
            String columnName,
            String description,
            String domainGetterMethod) {
        
        super(columnName, description);
        this.domainGetterMethod = domainGetterMethod;
    }

    public String getDomainGetterMethod() {
        return domainGetterMethod;
    }

    public HandleOutputResult handleOutput(List<CdbEntity> entities) {
        return handleOutput(entities, false);
    }
        
    public HandleOutputResult handleOutput(List<CdbEntity> entities, boolean useIdValues) {

        boolean isValid = true;
        String validString = "";
        List<ExportColumnData> columnDataList = new ArrayList<>();

        List<String> columnValues = new ArrayList<>();
        for (CdbEntity entity : entities) {
            // use reflection to retrieve value for column
            Object returnValue = null;
            String errorMsg = "";
            String methodName = getDomainGetterMethod();
            try {
                if ((methodName != null) && (!methodName.isBlank())) {
                    // use reflection to invoke setter method on entity instance
                    Method method;
//                Class paramType = getParamType();
                    method = entity.getClass().getMethod(methodName);
                    returnValue = method.invoke(entity);
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                // handled in null check below 
                errorMsg = ex.getClass().getName();
                isValid = false;
                validString
                        = "Unable to invoke getter method: " + methodName
                        + " for column: " + getColumnName()
                        + " reason: " + errorMsg;
                ValidInfo validInfo = new ValidInfo(isValid, validString);
                return new HandleOutputResult(validInfo, null);
            }
            
            String columnValue = "";
            if (returnValue != null) {
                if (useIdValues) {
                    if (returnValue instanceof List) {
                        List<CdbEntity> objList = (List<CdbEntity>) returnValue;
                        boolean isFirstItem = true;
                        for (CdbEntity obj : objList) {
                            if (!isFirstItem) {
                                columnValue = columnValue + ", ";
                            } else {
                                isFirstItem = false;
                            }
                            columnValue = columnValue + obj.getId().toString();
                        }
                        
                    } else if (returnValue instanceof CdbEntity) {
                        CdbEntity obj = (CdbEntity) returnValue;
                        columnValue = obj.getId().toString();
                    }
                } else {
                    columnValue = returnValue.toString();
                }
            }
            
            columnValues.add(columnValue);
        }
        
        ExportColumnData columnData = new ExportColumnData(getColumnName(), getDescription(), columnValues);
        columnDataList.add(columnData);
        ValidInfo validInfo = new ValidInfo(isValid, validString);
        return new HandleOutputResult(validInfo, columnDataList);
    }
}
