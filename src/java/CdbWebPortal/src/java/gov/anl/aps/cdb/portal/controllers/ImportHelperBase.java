/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author craig
 */
public abstract class ImportHelperBase<EntityType extends CdbEntity, EntityControllerType extends CdbEntityController> {

    public abstract class ColumnModel implements Serializable {

        protected String header;
        protected String property;
        protected boolean required = false;
        protected String setterMethod;
        protected String description;

        public ColumnModel(String h, String p, String s, boolean r, String d) {
            this.header = h;
            this.property = p;
            this.setterMethod = s;
            this.description = d;
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

        public String getDescription() {
            return description;
        }

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
        public ParseInfo parseCell(Cell cell) {
            return parseStringCell(cell);
        }
    }

    public class NumericColumnModel extends ColumnModel {

        public NumericColumnModel(String h, String p, String s, boolean r, String v) {
            super(h, p, s, r, v);
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

    protected List<EntityType> rows = new ArrayList<>();
    protected List<ColumnModel> columns = new ArrayList<>();
    protected byte[] templateExcelFile = null;
    protected boolean validInput = true;

    public ImportHelperBase() {
        createColumnModels();
    }

    public List<EntityType> getRows() {
        return rows;
    }

    public List<ColumnModel> getColumns() {
        return columns;
    }
    
    public boolean isValidInput() {
        return validInput;
    }

    protected boolean readXlsFileData(UploadedFile f) {

        InputStream inputStream;
        HSSFWorkbook workbook = null;
        try {
            inputStream = f.getInputstream();
            workbook = new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            return false;
        }

        HSSFSheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();

        parseSheet(rowIterator);

        return true;
    }

    protected boolean readXlsxFileData(UploadedFile f) {

        InputStream inputStream;
        XSSFWorkbook workbook = null;
        try {
            inputStream = f.getInputstream();
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            return false;
        }

        XSSFSheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            return false;
        }

        Iterator<Row> rowIterator = sheet.iterator();
        if (rowIterator == null) {
            return false;
        }

        parseSheet(rowIterator);
        return true;
    }

    protected void parseSheet(Iterator<Row> rowIterator) {

        String importName = "import-" + java.time.Instant.now().getEpochSecond();

        int rowCount = -1;
        int entityNum = 0;
        
        while (rowIterator.hasNext()) {

            rowCount = rowCount + 1;

            Row row = rowIterator.next();

            // skip header rows
            if (rowCount < getDataStartRow()) {
                continue;
            } else {
                entityNum = entityNum + 1;
            }

            CellType celltype;

            boolean result = parseRow(row, entityNum, importName);
            if (!result) {
                validInput = false;
            }
        }
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
        Drawing drawing = sheet.createDrawingPatriarch();
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.size() - 2; i++) {
            ColumnModel col = columns.get(i);

            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(col.getHeader());
            
            ClientAnchor anchor = createHelper.createClientAnchor();
            anchor.setCol1(headerCell.getColumnIndex());
            anchor.setCol2(headerCell.getColumnIndex() + 2);
            anchor.setRow1(headerRow.getRowNum());
            anchor.setRow2(headerRow.getRowNum() + 3);
            
            Comment headerComment = drawing.createCellComment(anchor);
            RichTextString str = createHelper.createRichTextString(col.getDescription());
            headerComment.setString(str);
            headerCell.setCellComment(headerComment);
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
        validInput = true;

        // allow subclass to reset
        reset_();
    }

    private String appendToString(String toString, String s) {
        String result = "";
        if (!toString.isEmpty()) {
            result = toString + ". ";
        }
        result = result + s;
        return result;
    }

    public boolean parseRow(Row row, int entityNum, String importName) {

        EntityType newEntity = createEntityInstance();
        boolean isValid = true;
        String validString = "";
        String uniqueId = importName + "-" + entityNum;

        for (int i = 0; i < columns.size() - 2; i++) {
            
            ColumnModel col = columns.get(i);
            String colName = col.getProperty();
            boolean required = col.isRequired();
            String setterMethodName = col.getSetterMethod();

            Cell cell;
            cell = row.getCell(i);

            ParseInfo result = col.parseCell(cell);

            if (!result.isValid()) {
                validString = appendToString(validString, result.getValidString());
                isValid = false;
            }
            
            // use reflection to invoke setter method on entity instance
            try {
                Method setterMethod;
                setterMethod = newEntity.getClass().getMethod(setterMethodName, String.class);
                setterMethod.invoke(newEntity, result.getValue());
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                validString = appendToString(validString, "Unable to invoke setter method: " + setterMethodName + " for column: " + colName + " reason: " + ex.getCause().getLocalizedMessage());
                isValid = false;
            }

        }
        
        String ppResult = postParse(newEntity, uniqueId);
        validString = appendToString(validString, ppResult);

        if (rows.contains(newEntity)) {
            validString = appendToString(validString, "Duplicate rows found in spreadsheet");
            isValid = false;
        } else {
            try {
                getEntityController().checkItemUniqueness(newEntity);
            } catch (CdbException ex) {
                validString = appendToString(validString, ex.getMessage());
                isValid = false;
            }
        }
        
        newEntity.setIsValidImport(isValid);
        newEntity.setValidStringImport(validString);
        
        rows.add(newEntity);
        return isValid;
    }
    
    protected String postParse(EntityType e, String id) {
        // by default do nothing, subclasses can override to customize
        return "";
    }

    public ImportInfo importData() {

        EntityControllerType controller = this.getEntityController();

        String message = "";
        try {
            controller.createList(rows);
            message = "Import succeeded, created " + rows.size() + " instances";
        } catch (CdbException | RuntimeException ex) {
            Throwable t = ExceptionUtils.getRootCause(ex);
            return new ImportInfo(false, "Import failed. " + ex.getMessage() + ": " + t.getMessage() + ".");
        }
        
        String result = postImport();
        message = appendToString(message, result);
        
        return new ImportInfo(true, message);
    }
    
    protected String postImport() {
        // by default do nothing, subclasses can override to customize
        return "";
    }

    protected abstract void createColumnModels_();

    public abstract int getDataStartRow();

    protected abstract String getCompletionUrlValue();

    public abstract EntityControllerType getEntityController();
    
    public abstract String getTemplateFilename();
    
    protected abstract EntityType createEntityInstance();
}
