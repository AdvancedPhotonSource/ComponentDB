/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
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

    public abstract class ColumnModel implements Serializable {

        protected String header;
        protected String property;
        protected boolean required = false;
        protected String setterMethod;
        protected String sampleValue;

        public ColumnModel(String h, String p, String s, boolean r, String v) {
            this.header = h;
            this.property = p;
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

        public String getSetterMethod() {
            return setterMethod;
        }

        public boolean isRequired() {
            return this.required;
        }

        public String getSampleValue() {
            return sampleValue;
        }

        public abstract void setTemplateCell(Cell dataCell);

        public abstract ParseInfo parseCell(Cell cell);

        protected ParseInfo parseStringCell(Cell cell) {

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
                validString = "Required value missing for " + header;
            }

            return new ParseInfo(parsedValue, isValid, validString);
        }
    }

    public class StringColumnModel extends ColumnModel {

        public StringColumnModel(String h, String p, String s, boolean r, String v) {
            super(h, p, s, r, v);
        }

        @Override
        public void setTemplateCell(Cell dataCell) {
            dataCell.setCellType(CellType.STRING);
            dataCell.setCellValue(getSampleValue());
        }

        @Override
        public ParseInfo parseCell(Cell cell) {
            return parseStringCell(cell);
        }
    }

    public class NumericColumnModel extends ColumnModel {

        public NumericColumnModel(String h, String p, String s, boolean r, String v) {
            super(h, p, s, r, v);
        }

        @Override
        public void setTemplateCell(Cell dataCell) {
            dataCell.setCellType(CellType.NUMERIC);
            dataCell.setCellValue(Double.valueOf(getSampleValue()));
        }

        @Override
        public ParseInfo parseCell(Cell cell) {
            String parsedValue = "";
            boolean isValid = true;
            String validString = "";

            if (cell == null) {
                parsedValue = "";
            } else if (cell.getCellType() != CellType.NUMERIC) {
                parsedValue = "";
                isValid = false;
                validString = header + " is not a number";
            } else {
                parsedValue = String.valueOf(cell.getNumericCellValue());
            }

            return new ParseInfo(parsedValue, isValid, validString);
        }
    }

    public class UrlColumnModel extends ColumnModel {

        public UrlColumnModel(String h, String p, String s, boolean r, String v) {
            super(h, p, s, r, v);
        }

        @Override
        public void setTemplateCell(Cell dataCell) {
            dataCell.setCellType(CellType.STRING);
            dataCell.setCellValue(getSampleValue());
        }

        @Override
        public ParseInfo parseCell(Cell cell) {
            ParseInfo result = parseStringCell(cell);
            if (result.isValid) {
                if (result.getValue().length() > 256) {
                    result.isValid(false);
                    result.setValidString("URL length exceeds 256 characters for " + header);
                }
            }

            return result;
        }
    }

    public class IdRefColumnModel extends ColumnModel {
        
        private CdbEntityController controller;

        public IdRefColumnModel(String h, String p, String s, boolean r, String v, CdbEntityController c) {
            super(h, p, s, r, v);
            controller = c;
        }

        @Override
        public void setTemplateCell(Cell dataCell) {
            dataCell.setCellType(CellType.STRING);
            dataCell.setCellValue(getSampleValue());
        }

        @Override
        public ParseInfo parseCell(Cell cell) {
            ParseInfo result = parseStringCell(cell);
            if ((result.isValid) && (result.getValue().length() > 0)) {
                if (controller.findById(Integer.valueOf(result.getValue())) == null) {
                    result.isValid(false);
                    result.setValidString("Unable to find object for: " + header + " with id: " + result.getValue());
                }
            }
            return result;
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

    protected List<CdbEntity> rows = new ArrayList<>();
    protected List<ColumnModel> columns = new ArrayList<>();
    protected byte[] templateExcelFile = null;

    public ImportHelperBase() {
        createColumnModels();
    }

    public List<CdbEntity> getRows() {
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
        return new DefaultStreamedContent(inStream, "xls", getTemplateFilename() + ".xls");
    }

    private void buildTemplateExcelFile() {

        Workbook wb = new XSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet("template");
        Row headerRow = sheet.createRow(0);
        Row dataRow = sheet.createRow(1);
        for (int i = 0; i < columns.size() - 2; i++) {
            ColumnModel col = columns.get(i);

            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(col.getHeader());

            Cell dataCell = dataRow.createCell(i);

            col.setTemplateCell(dataCell);
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

        columns.add(new StringColumnModel(isValidHeader, isValidProperty, "isValidImport", false, ""));
        columns.add(new StringColumnModel(validStringHeader, validStringProperty, "setValidStringImport", false, ""));
    }

    protected void reset_() {
        // allow subclass to reset, by default do nothing
    }

    public void reset() {
        rows.clear();

        // allow subclass to reset
        reset_();
    }

    private String appendToValidString(String validString, String s) {
        String result = "";
        if (!validString.isEmpty()) {
            result = validString + ". ";
        }
        result = result + s;
        return result;
    }

    public boolean parseRow(Row row) {

        CdbEntity newEntity = getEntityController().createEntityInstance();
        boolean isValid = true;
        String validString = "";

        for (int i = 0; i < columns.size() - 2; i++) {

            ColumnModel col = columns.get(i);
            String colName = col.getProperty();
            boolean required = col.isRequired();
            String setterMethodName = col.getSetterMethod();

            Cell cell;
            cell = row.getCell(i);

            ParseInfo result = col.parseCell(cell);

            if (!result.isValid()) {
                validString = appendToValidString(validString, result.getValidString());
                isValid = false;
            }

            // use reflection to invoke setter method on entity instance
            try {
                Method setterMethod;
                setterMethod = newEntity.getClass().getMethod(setterMethodName, String.class);
                setterMethod.invoke(newEntity, result.getValue());
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                validString = appendToValidString(validString, "Unable to invoke setter method: " + setterMethodName + " for column: " + colName + " reason: " + ex.toString());
                isValid = false;
            }

        }

        if (rows.contains(newEntity)) {
            validString = appendToValidString(validString, "Duplicate rows found in spreadsheet");
            isValid = false;
        } else {
            try {
                getEntityController().checkItemUniqueness(newEntity);
            } catch (CdbException ex) {
                validString = appendToValidString(validString, ex.getMessage());
                isValid = false;
            }
        }

        newEntity.setIsValidImport(isValid);
        newEntity.setValidStringImport(validString);

        rows.add(newEntity);
        return isValid;
    }

    public ImportInfo importData() {

        CdbEntityController controller = this.getEntityController();

        String message = "";
        List<CdbEntity> newItems = new ArrayList<>();
        for (CdbEntity row : rows) {
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

    public abstract CdbEntityController getEntityController();
    
    public abstract String getTemplateFilename();
}
