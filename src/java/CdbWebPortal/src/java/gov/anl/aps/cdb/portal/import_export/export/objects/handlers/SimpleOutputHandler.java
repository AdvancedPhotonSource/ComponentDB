/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects.handlers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.import_export.export.objects.ColumnValueResult;
import gov.anl.aps.cdb.portal.import_export.export.objects.ExportColumnData;
import gov.anl.aps.cdb.portal.import_export.export.objects.HandleOutputResult;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ExportMode;
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
    protected String domainTransferGetterMethod = null;
    
    public SimpleOutputHandler(
            String columnName,
            String description,
            String domainGetterMethod,
            String domainTransferGetterMethod) {
        
        super(columnName, description);
        this.domainGetterMethod = domainGetterMethod;
        this.domainTransferGetterMethod = domainTransferGetterMethod;
    }

    public String getDomainGetterMethod() {
        return domainGetterMethod;
    }

    public String getDomainTransferGetterMethod() {
        return domainTransferGetterMethod;
    }

    protected ColumnValueResult getColumnValue(CdbEntity entity, ExportMode exportMode) {
        
        boolean isValid = true;
        String validString = "";

        // use reflection to retrieve value for column
        Object returnValue = null;
        String errorMsg = "";
        
        // determine getter method base on mode
        String methodName = null;
        if (exportMode == ExportMode.TRANSFER) {
            // default to regular export getter method if transfer method not specified
            methodName = getDomainTransferGetterMethod();
            if (methodName == null) {
                methodName = getDomainGetterMethod();
            }
        } else {
            methodName = getDomainGetterMethod();
        }
        
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
            return new ColumnValueResult(validInfo, null);
        }

        String columnValue = null;
        try {
            columnValue = formatCellValue(returnValue, exportMode);
        } catch (CdbException ex) {
            isValid = false;
            validString = ex.getMessage();
        }
        
        ValidInfo validInfo = new ValidInfo(isValid, validString);
        return new ColumnValueResult(validInfo, columnValue);
    }
    
    protected String formatCellValue(Object value, ExportMode exportMode) throws CdbException {  
        if (value != null) {
            return value.toString();
        } else {
            return "";
        }
    }

    public ColumnValueResult handleOutput(CdbEntity entity) {
        return getColumnValue(entity, ExportMode.EXPORT);
    }

    @Override
    public HandleOutputResult handleOutput(List<CdbEntity> entities, ExportMode exportMode) {
        boolean useIdValues;
        if (exportMode == ExportMode.TRANSFER) {
            useIdValues = false;
        } else {
            useIdValues = true;
        }
        return handleOutput(entities, exportMode, useIdValues);
    }
    
    public HandleOutputResult handleOutput(List<CdbEntity> entities, ExportMode exportMode, boolean useIdValues) {

        boolean isValid = true;
        String validString = "";
        List<ExportColumnData> columnDataList = new ArrayList<>();

        List<String> columnValues = new ArrayList<>();
        for (CdbEntity entity : entities) {
            ColumnValueResult columnValueResult = getColumnValue(entity, exportMode);
            if (!columnValueResult.getValidInfo().isValid()) {
                return new HandleOutputResult(columnValueResult.getValidInfo(), null);
            } else {
                columnValues.add(columnValueResult.getColumnValue());
            }
        }
        
        ExportColumnData columnData = new ExportColumnData(getColumnName(), getDescription(), columnValues);
        columnDataList.add(columnData);
        
        ValidInfo validInfo = new ValidInfo(isValid, validString);
        return new HandleOutputResult(validInfo, columnDataList);
    }
}
