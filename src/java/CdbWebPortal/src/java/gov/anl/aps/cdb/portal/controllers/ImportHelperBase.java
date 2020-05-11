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
        
        public abstract Class getParamType();

        protected String parseStringCell(Cell cell) {

            String parsedValue = "";

            if (cell == null) {
                parsedValue = "";
            } else {
                cell.setCellType(CellType.STRING);
                parsedValue = cell.getStringCellValue();
            }

            return parsedValue;
        }
    }

    public class StringColumnModel extends ColumnModel {

        protected int maxLength = 0;
        
        public StringColumnModel(String h, String p, String s, boolean r, String v, int l) {
            super(h, p, s, r, v);
            maxLength = l;
        }

        public int getMaxLength() {
            return maxLength;
        }

        @Override
        public ParseInfo parseCell(Cell cell) {
            boolean isValid = true;
            String validString = "";
            String value = parseStringCell(cell);
            if ((getMaxLength() > 0) && (value.length() > getMaxLength())) {
                isValid = false;
                validString = appendToString(validString, "Value length exceeds " + getMaxLength() + " characters for column " + header);
            }
            return new ParseInfo<>(value, isValid, validString);
        }
        
        @Override
        public Class getParamType() {
            return String.class;
        }
    }

    public class NumericColumnModel extends ColumnModel {

        public NumericColumnModel(String h, String p, String s, boolean r, String v) {
            super(h, p, s, r, v);
        }

        @Override
        public ParseInfo parseCell(Cell cell) {
            Double parsedValue = null;
            boolean isValid = true;
            String validString = "";

            if (cell == null) {
                parsedValue = null;
            } else if (cell.getCellType() != CellType.NUMERIC) {
                parsedValue = null;
                isValid = false;
                validString = header + " is not a number";
            } else {
                parsedValue = cell.getNumericCellValue();
            }

            return new ParseInfo<>(parsedValue, isValid, validString);
        }
        
        @Override
        public Class getParamType() {
            return Double.class;
        }
    }

    public class BooleanColumnModel extends ColumnModel {

        public BooleanColumnModel(String h, String p, String s, boolean r, String v) {
            super(h, p, s, r, v);
        }

        @Override
        public ParseInfo parseCell(Cell cell) {
            Boolean parsedValue = null;
            boolean isValid = true;
            String validString = "";

            if (cell == null) {
                parsedValue = null;
            } else if (cell.getCellType() != CellType.BOOLEAN) {
                String stringValue = parseStringCell(cell);
                if (stringValue.length() == 0) {
                    parsedValue = null;
                    isValid = true;
                    validString = "";
                } else {
                    if (stringValue.equalsIgnoreCase("true") || stringValue.equals("1")) {
                        parsedValue = true;
                    } else if (stringValue.equalsIgnoreCase("false") || stringValue.equals("0")) {
                        parsedValue = false;
                    } else {
                        parsedValue = null;
                        isValid = false;
                        validString = "unexpected boolean value: " + stringValue +
                                " for column: " + header;
                    }
                }
            } else {
                parsedValue = cell.getBooleanCellValue();
            }

            return new ParseInfo<>(parsedValue, isValid, validString);
        }
        
        @Override
        public Class getParamType() {
            return Boolean.class;
        }
    }
    
    public abstract class RefColumnModel extends ColumnModel {
        
        protected CdbEntityController controller;
        protected Class paramType;

        public RefColumnModel(String h, String p, String s, boolean r, String v, CdbEntityController c, Class pType) {
            super(h, p, s, r, v);
            controller = c;
            paramType = pType;
        }
        
        @Override
        public Class getParamType() {
            return paramType;
        }
    }

    public class IdRefColumnModel extends RefColumnModel {
        
        public IdRefColumnModel(String h, String p, String s, boolean r, String v, CdbEntityController c, Class pType) {
            super(h, p, s, r, v, c, pType);
        }

        @Override
        public ParseInfo parseCell(Cell cell) {
            String strValue = parseStringCell(cell);
            Object objValue = null;
            if (strValue.length() > 0) {
                objValue = controller.findById(Integer.valueOf(strValue));
                if (objValue == null) {
                    String msg = "Unable to find object for: " + header + 
                            " with id: " + strValue;
                    return new ParseInfo<>(objValue, false, msg);
                } else {
                    return new ParseInfo<>(objValue, true, "");
                }
            }
            return new ParseInfo<>(objValue, true, "");
        }
    }

    public class IdOrNameRefColumnModel extends RefColumnModel {
        
        String domainName = null;
        
        public IdOrNameRefColumnModel(String h, String p, String s, boolean r, String v, CdbEntityController c, Class pType, String domainName) {
            super(h, p, s, r, v, c, pType);
            this.domainName = domainName;
        }

        @Override
        public ParseInfo parseCell(Cell cell) {
            
            String strValue = parseStringCell(cell);
            Object objValue = null;
            if ((strValue != null) && (!strValue.isEmpty())) {
                try {
                    objValue = controller.findUniqueByIdOrName(strValue, domainName);
                } catch (CdbException ex) {
                    String msg = "Unable to find object by id or name: " + strValue
                            + " reason: " + ex.getMessage();
                    return new ParseInfo<>(objValue, false, msg);
                }
                if (objValue == null) {
                    String msg = "Unable to find object for: " + header
                            + " with id or name: " + strValue;
                    return new ParseInfo<>(objValue, false, msg);
                }
            }
            return new ParseInfo<>(objValue, true, "");
        }
    }

    static public class ValidInfo {

        protected boolean isValid = false;
        protected String validString = "";
        protected boolean isDuplicate = false;

        public ValidInfo(boolean iv, String s) {
            isValid = iv;
            validString = s;
        }
        
        public ValidInfo(boolean iv, String s, boolean d) {
            this(iv, s);
            isDuplicate = d;
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
        
        public boolean isDuplicate() {
            return isDuplicate;
        }
    }
    
    static public class ParseInfo<ValueType extends Object> {

        protected ValueType value = null;
        protected ValidInfo validInfo = null;

        public ParseInfo(ValueType v, boolean iv, String s) {
            value = v;
            validInfo = new ValidInfo(iv, s);
        }

        public ParseInfo(ValueType v, boolean iv, String s, boolean d) {
            value = v;
            validInfo = new ValidInfo(iv, s, d);
        }

        public ValidInfo getValidInfo() {
            return validInfo;
        }
        
        public ValueType getValue() {
            return value;
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
    protected String validationMessage = "";

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
    
    public String getValidationMessage() {
        return validationMessage;
    }

    public String getCompletionUrl() {
        return getCompletionUrlValue();
    }

    protected void createColumnModels() {

        // allow subclass to create column models
        createColumnModels_();

        // these are special columns just for displaying validation info for each row, they are not parsed so treated specially in parsing code
        columns.add(new StringColumnModel(isValidHeader, isValidProperty, "isValidImport", false, "", 0));
        columns.add(new StringColumnModel(validStringHeader, validStringProperty, "setValidStringImport", false, "", 0));
    }

    protected void reset_() {
        // allow subclass to reset, by default do nothing
    }

    public void reset() {
        rows.clear();
        validInput = true;
        validationMessage = "";

        // allow subclass to reset
        reset_();
    }

    protected String appendToString(String toString, String s) {
        String result = "";
        if (!toString.isEmpty()) {
            result = toString + ". ";
        }
        result = result + s;
        return result;
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
        int dupCount = 0;
        String dupString = "";
        
        while (rowIterator.hasNext()) {

            rowCount = rowCount + 1;
            Row row = rowIterator.next();

            if (rowCount == 0) {
                // parse header row
                ValidInfo headerValidInfo = parseHeader(row);
                if (!headerValidInfo.isValid()) {
                    validInput = false;
                    validationMessage = "Warning: " + headerValidInfo.getValidString() + 
                            ". Please make sure spreadsheet format is correct and enter values in all header rows before proceeding";
                }
            } else {
                entityNum = entityNum + 1;
                ValidInfo rowValidInfo = parseRow(row, entityNum, importName);
                if (!rowValidInfo.isValid()) {
                    validInput = false;
                }
                if (rowValidInfo.isDuplicate()) {
                    dupCount = dupCount + 1;
                    dupString = appendToString(dupString, rowValidInfo.getValidString());
                }
            }
        }
        
        if (dupCount > 0) {
            validationMessage = appendToString(validationMessage, "Note: removed " + dupCount + " rows that already exist in database: (" + dupString + ")");
        }
        if (rows.size() == 0) {
            // nothing to import, this will disable the "next" button
            validInput = false;
            validationMessage = appendToString(validationMessage, "Note: nothing to import");
        }
    }
    
    /**
     * Checks that the number of values present in the header row matches the
     * expected number of columns.
     * @param row
     * @return 
     */
    protected ValidInfo parseHeader(Row row) {
        boolean isValid = true;
        String validMessage = "";
        
        int headerValues = 0;
        for (int i = 0; i < columns.size() - 2; i++) {
            // check if header value present for each column
            Cell cell;
            cell = row.getCell(i);        
            cell.setCellType(CellType.STRING);
            String value = cell.getStringCellValue();
            
            if ((value != null) && (!value.isEmpty())) {
                headerValues = headerValues + 1;
            }
        }        
        if (headerValues != columns.size() - 2) {
            isValid = false;
            validMessage = "Header row contains fewer column values than number of columns in template format";
        }
        
        int extraValues = 0;
        for (int i = columns.size() - 2; i < columns.size() + 3; i++) {
            // check for extra values beyond the expected columns
            Cell cell;
            cell = row.getCell(i);  
            if (cell != null) {
                cell.setCellType(CellType.STRING);
                String value = cell.getStringCellValue();

                if ((value != null) && (!value.isEmpty())) {
                    extraValues = extraValues + 1;
                }
            }
        }
        if (extraValues > 0) {
            isValid = false;
            validMessage = "Header row contains more column values than number of columns in template format";
        }
        
        return new ValidInfo(isValid, validMessage);
    }

    protected ValidInfo parseRow(Row row, int entityNum, String importName) {

        EntityType newEntity = createEntityInstance();
        boolean isValid = true;
        String validString = "";
        String uniqueId = importName + "-" + entityNum;
        boolean isDuplicate = false;

        for (int i = 0; i < columns.size() - 2; i++) {
            
            ColumnModel col = columns.get(i);
            String colName = col.getHeader();
            boolean required = col.isRequired();
            String setterMethodName = col.getSetterMethod();

            Cell cell;
            cell = row.getCell(i);

            ParseInfo result = col.parseCell(cell);
            ValidInfo validInfo = result.getValidInfo();
            
            if (!validInfo.isValid()) {
                validString = appendToString(validString, validInfo.getValidString());
                isValid = false;
            }
            
            ParseInfo ppCellResult = postParseCell(result.getValue(), colName, uniqueId);
            if (!ppCellResult.getValidInfo().isValid()) {
                validString = appendToString(validString, ppCellResult.getValidInfo().getValidString());
                isValid = false;
            }
            Object parsedValue = ppCellResult.getValue();
            
            if (required && (parsedValue == null)) {
                isValid = false;
                validString = appendToString(validString, "Required value missing for " + colName);
            }
                 
            // use reflection to invoke setter method on entity instance
            if (parsedValue != null) {
                try {
                    Method setterMethod;
                    Class paramType = col.getParamType();
                    setterMethod = newEntity.getClass().getMethod(setterMethodName, paramType);
                    setterMethod.invoke(newEntity, parsedValue);
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    validString = appendToString(validString, "Unable to invoke setter method: " + setterMethodName + " for column: " + colName + " reason: " + ex.getClass().getName());
                    isValid = false;
                }
            }
        }
        
        ValidInfo ppResult = postParseRow(newEntity, uniqueId);
        if (!ppResult.isValid()) {
            validString = appendToString(validString, ppResult.getValidString());
            isValid = false;
        }

        if (rows.contains(newEntity)) {
            validString = appendToString(validString, "Duplicate rows found in spreadsheet");
            isValid = false;
        } else {
            try {
                getEntityController().checkItemUniqueness(newEntity);
            } catch (CdbException ex) {
                if (ignoreDuplicates()) {
                    isDuplicate = true;
                } else {
                    validString = appendToString(validString, ex.getMessage());
                    isValid = false;
                }
            }
        }
        
        newEntity.setIsValidImport(isValid);
        newEntity.setValidStringImport(validString);
        
        if (!isDuplicate) {
            rows.add(newEntity);
            return new ValidInfo(isValid, validString, false);
        } else {
            return new ValidInfo(true, newEntity.toString(), true);
        }
    }
    
    /**
     * Provides callback for helper subclass to handle the parsed value for a 
     * particular column, e.g., to perform string replacement on the value or
     * additional validation. Default behavior is to do nothing, subclasses
     * override to customize.
     * @param parsedValue
     * @param columnName
     * @param uniqueId
     * @return 
     */
    protected ParseInfo postParseCell(Object parsedValue, String columnName, String uniqueId) {
        return new ParseInfo(parsedValue, true, "");
    }
    
    /**
     * Provides callback for helper subclass to handle the result of a parsed row
     * at the entity level.  The subclass can update one field of the entity based
     * on the value in some other field, for example.  Default behavior is to
     * do nothing, subclasses override to customize.
     * @param e
     * @param id
     * @return 
     */
    protected ValidInfo postParseRow(EntityType e, String id) {
        return new ValidInfo(true, "");
    }

    public ImportInfo importData() {

        EntityControllerType controller = this.getEntityController();

        String message = "";
        try {
            controller.createList(rows);
            message = "Import succeeded, created " + rows.size() + " instances";
        } catch (CdbException | RuntimeException ex) {
            Throwable t = ExceptionUtils.getRootCause(ex);
            return new ImportInfo(false, "Import failed. " + ex.getClass().getName());
        }
        
        ValidInfo result = postImport();
        message = appendToString(message, result.getValidString());
        
        return new ImportInfo(true, message);
    }
    
    /**
     * Provides callback for helper subclass to do post processing after the
     * import commit completes.  For example, the helper may need to update some
     * attribute of the imported rows based on the identifier created during the
     * database commit. Default behavior is to do nothing, subclasses override to
     * customize.
     * @return 
     */
    protected ValidInfo postImport() {
        return new ValidInfo(true, "");
    }
    
    /**
     * Specifies whether the helper class ignores rows in the input data that
     * duplicate existing database rows.  Default behavior is to not ignore
     * duplicates, subclasses override to change the default.
     * @return 
     */
    protected boolean ignoreDuplicates() {
        return false;
    }

    protected abstract void createColumnModels_();

    protected abstract String getCompletionUrlValue();

    public abstract EntityControllerType getEntityController();
    
    public abstract String getTemplateFilename();
    
    protected abstract EntityType createEntityInstance();
}
