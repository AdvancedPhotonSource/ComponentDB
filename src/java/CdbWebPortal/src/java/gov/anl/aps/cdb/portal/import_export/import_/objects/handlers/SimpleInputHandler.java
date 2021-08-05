/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.portal.import_export.import_.objects.ParseInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author craig
 */
public abstract class SimpleInputHandler extends SingleColumnInputHandler {

    private static final Logger LOGGER = LogManager.getLogger(SimpleInputHandler.class.getName());

    protected String propertyName = null;
    protected String setterMethod = null;
    
    public SimpleInputHandler(String columnName) {
        super(columnName);
    }

    public SimpleInputHandler(
            int columnIndex,
            String columnName,
            String propertyName,
            String setterMethod) {
        
        super(columnIndex, columnName);
        this.propertyName = propertyName;
        this.setterMethod = setterMethod;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getSetterMethod() {
        return setterMethod;
    }

    public abstract ParseInfo parseCellValue(String cellValue);

    public abstract Class getParamType();

    @Override
    public ValidInfo handleInput(
            Row row,
            Map<Integer, String> cellValueMap,
            Map<String, Object> rowMap) {

        boolean isValid = true;
        String validString = "";

        String cellValue = cellValueMap.get(getColumnIndex());

        if (cellValue != null && (!cellValue.isEmpty())) {

            // process the parsed value
            ParseInfo result = parseCellValue(cellValue);
            Object parsedValue = result.getValue();
            if (!result.getValidInfo().isValid()) {
                isValid = false;
                validString = result.getValidInfo().getValidString();
            }

            // add to row dictionary
            rowMap.put(getPropertyName(), parsedValue);
        }

        return new ValidInfo(isValid, validString);
    }

    @Override
    public ValidInfo updateEntity(Map<String, Object> rowMap, CdbEntity entity) {

        boolean isValid = true;
        String validString = "";
        String methodLogName = "SimpleInputHandler::updateEntity() ";

        // get row dictionary value
        Object parsedValue = rowMap.get(getPropertyName());
            
        try {
            String setterMethodName = getSetterMethod();
            if ((setterMethodName != null) && (!setterMethodName.equals(""))) {
                // use reflection to invoke setter method on entity instance
                Method method;
                Class paramType = getParamType();
                method = entity.getClass().getMethod(getSetterMethod(), paramType);
                method.invoke(entity, parsedValue);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            isValid = false;
            validString
                    = "Unable to invoke setter method: " + getSetterMethod()
                    + " for column: " + getColumnName()
                    + " reason: " + ex.getClass().getName();
            LOGGER.info(methodLogName + validString);
            return new ValidInfo(isValid, validString);
        }

        return new ValidInfo(isValid, validString);
    }
}
