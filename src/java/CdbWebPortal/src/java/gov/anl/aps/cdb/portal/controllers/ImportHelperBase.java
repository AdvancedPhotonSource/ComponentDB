/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author craig
 */
public abstract class ImportHelperBase {

    public enum ColType {
        STRING,
        NUMERIC,
        URL
    }

    public class ColumnModel implements Serializable {

        private String header;
        private String property;
        private ColType colType;
        private boolean required = false;
        private String setterMethod;
        private String sampleValue;

        public ColumnModel(String h, String p, ColType t, String s, boolean r, String v) {
            this.header = h;
            this.property = p;
            this.colType = t;
            this.setterMethod = s;
            this.sampleValue = v;
            this.required = r;
        }

        public String getHeader() {
            return header;
        }

        public String getProperty() {
            return property;
        }

        public ColType getColType() {
            return colType;
        }

        public String getSetterMethod() {
            return setterMethod;
        }

        public boolean isRequired() {
            return this.required;
        }

        public String getSampleValue() {
            return sampleValue;
        }
    }

    static public class ParseInfo {

        protected String value = "";
        protected boolean isValid = false;
        protected String validString = "";

        public ParseInfo() {
        }

        public ParseInfo(String v, boolean iv, String s) {
            value = v;
            isValid = iv;
            validString = s;
        }

        public String getValue() {
            return value;
        }

        public boolean isValid() {
            return isValid;
        }
        
        public void isValid(boolean b) {
            isValid = b;
        }

        public String getValidString() {
            return validString;
        }
        
        public void setValidString(String s) {
            validString = s;
        }
    }

    static public class ImportInfo {

        protected boolean importSuccessful = true;
        protected String message = "";

        public ImportInfo(boolean s, String m) {
            importSuccessful = s;
            message = m;
        }

        public boolean isImportSuccessful() {
            return importSuccessful;
        }

        public String getMessage() {
            return message;
        }
    }

    protected static String isValidHeader = "Is Valid";
    protected static String isValidProperty = "isValidImport";
    protected static String validStringHeader = "Valid String";
    protected static String validStringProperty = "validStringImport";

    protected List<Item> rows = new ArrayList<>();
    protected List<ColumnModel> columns = new ArrayList<>();
    protected byte[] templateExcelFile = null;

    public ImportHelperBase() {
        createColumnModels();
    }

    public List<Item> getRows() {
        return rows;
    }

    public List<ColumnModel> getColumns() {
        return columns;
    }

    public StreamedContent getTemplateExcelFile() {
        if (templateExcelFile == null) {
            buildTemplateExcelFile();
        }
        InputStream inStream = new ByteArrayInputStream(templateExcelFile);
        return new DefaultStreamedContent(inStream, "xls", "cableCatalogTemplate.xls");
    }

    private void buildTemplateExcelFile() {

        Workbook wb = new XSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet("cable type specs");
        Row headerRow = sheet.createRow(0);
        Row dataRow = sheet.createRow(1);
        for (int i = 0; i < columns.size() - 2; i++) {
            ColumnModel col = columns.get(i);

            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(col.getHeader());

            Cell dataCell = dataRow.createCell(i);

            if (col.getColType() == ColType.STRING) {
                dataCell.setCellType(CellType.STRING);
                dataCell.setCellValue(col.getSampleValue());
            } else if (col.getColType() == ColType.NUMERIC) {
                dataCell.setCellType(CellType.NUMERIC);
                dataCell.setCellValue(Double.valueOf(col.getSampleValue()));
            }
        }

        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
            wb.write(outStream);
            templateExcelFile = outStream.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(ImportHelperBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getCompletionUrl() {
        return getCompletionUrlValue();
    }

    protected void createColumnModels() {

        // allow subclass to create column models
        createColumnModels_();

        columns.add(new ColumnModel(isValidHeader, isValidProperty, ColType.STRING, "isValidImport", false, ""));
        columns.add(new ColumnModel(validStringHeader, validStringProperty, ColType.STRING, "setValidStringImport", false, ""));
    }

    protected void reset_() {
        // allow subclass to reset, by default do nothing
    }

    public void reset() {
        rows.clear();

        // allow subclass to reset
        reset_();
    }

    private ParseInfo parseStringCell(Cell cell, String colName, boolean required) {

        String parsedValue = "";
        boolean isValid = true;
        String validString = "";

        if (cell == null) {
            parsedValue = "";
        } else {
            cell.setCellType(CellType.STRING);
            parsedValue = cell.getStringCellValue();
        }

        if (required && parsedValue.equals("")) {
            isValid = false;
            validString = "required value missing for " + colName;
        }

        return new ParseInfo(parsedValue, isValid, validString);
    }

    private ParseInfo parseNumericCell(Cell cell, String colName, boolean required) {
        
        String parsedValue = "";
        boolean isValid = true;
        String validString = "";

        if (cell == null) {
            parsedValue = "";
        } else if (cell.getCellType() != CellType.NUMERIC) {
            parsedValue = "";
            isValid = false;
            validString = colName + " is not a number";
        } else {
            parsedValue = String.valueOf(cell.getNumericCellValue());
        }
        
        return new ParseInfo(parsedValue, isValid, validString);
    }

    private ParseInfo parseUrlCell(Cell cell, String colName, boolean required) {
        ParseInfo result = parseStringCell(cell, colName, required);
        if (result.isValid) {
            if (result.getValue().length() > 256) {
                result.isValid(false);
                result.setValidString("URL length exceeds 256 characters for " + colName);
            }
        }
        
        return result;
    }

    public boolean parseRow(Row row) {

        Item newEntity = getEntityController().createEntityInstance();
        boolean isValid = true;
        String validString = "";

        for (int i = 0; i < columns.size() - 2; i++) {

            ColumnModel col = columns.get(i);
            String colName = col.getProperty();
            ColType colType = col.getColType();
            boolean required = col.isRequired();
            String setterMethodName = col.getSetterMethod();

            Cell cell;
            cell = row.getCell(i);

            ParseInfo result = new ParseInfo();

            switch (colType) {

                case STRING:
                    result = parseStringCell(cell, colName, required);
                    break;

                case NUMERIC:
                    result = parseNumericCell(cell, colName, required);
                    break;

                case URL:
                    result = parseUrlCell(cell, colName, required);
                    break;
            }

            if (!result.isValid()) {
                if (!validString.isEmpty()) {
                    validString = validString + ". ";
                }
                validString = validString + result.getValidString();
                isValid = false;
            }

            // use reflection to invoke setter method on entity instance
            try {
                Method setterMethod;
                setterMethod = newEntity.getClass().getMethod(setterMethodName, String.class);
                setterMethod.invoke(newEntity, result.getValue());
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(ImportHelperBase.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(ImportHelperBase.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ImportHelperBase.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ImportHelperBase.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(ImportHelperBase.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        if (rows.contains(newEntity)) {
            isValid = false;
            validString = "duplicate rows found in spreadsheet";
        } else {
            try {
                getEntityController().checkItemUniqueness(newEntity);
            } catch (CdbException ex) {
                isValid = false;
                validString = "duplicate found in database";
            }
        }
        
        newEntity.setIsValidImport(isValid);
        newEntity.setValidStringImport(validString);
        
        rows.add(newEntity);
        return isValid;
    }
    
    public ImportInfo importData() {
        
        ItemController controller = this.getEntityController();
        
        String message = "";
        List<Item> newItems = new ArrayList<>();
        for (Item row : rows) {
            newItems.add(row);
        }
        
        try {
            controller.createList(newItems);
            return new ImportInfo(true, "Import succeeded.  Created " + rows.size() + " instances.");
        } catch (CdbException ex) {
            return new ImportInfo(false, "Import failed. " + ex.getMessage() + ".");
        } catch (RuntimeException ex) {
            Throwable t = ExceptionUtils.getRootCause(ex);
            return new ImportInfo(false, "Import failed. " + ex.getMessage() + ": " + t.getMessage() + ".");
        }
    }



    protected abstract void createColumnModels_();

    public abstract int getDataStartRow();

    protected abstract String getCompletionUrlValue();

    protected abstract boolean isValidationOnly();

    public abstract ItemController getEntityController();
}
