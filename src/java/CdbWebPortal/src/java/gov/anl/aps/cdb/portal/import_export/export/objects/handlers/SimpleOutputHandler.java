/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects.handlers;

import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;

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
            int columnIndex,
            String columnName,
            String domainGetterMethod) {
        
        super(columnIndex, columnName);
        this.domainGetterMethod = domainGetterMethod;
    }

    public String getDomainGetterMethod() {
        return domainGetterMethod;
    }

    public ValidInfo handleOutput(
            Row row,
            Map<Integer, String> cellValueMap,
            CdbEntity entity) {

        boolean isValid = true;
        String validString = "";

        // use reflection to retrieve value for column
        Object returnValue = null;
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
            isValid = false;
            validString
                    = "Unable to invoke getter method: " + methodName
                    + " for column: " + getColumnName()
                    + " reason: " + ex.getClass().getName();
            return new ValidInfo(isValid, validString);
        }
        
        // write value to output map
        if (returnValue != null) {
            cellValueMap.put(getColumnIndex(), returnValue.toString());
        }

        return new ValidInfo(isValid, validString);
     }
}
