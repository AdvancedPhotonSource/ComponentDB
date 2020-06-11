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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author craig
 */
public abstract class ImportHelperBase<EntityType extends CdbEntity, EntityControllerType extends CdbEntityController> {

    protected abstract class ColumnSpec {
        
        private int columnIndex;
        private String header;
        private String propertyName;
        private String entitySetterMethod;
        private boolean required;
        private String description;
        
        public ColumnSpec(
                int columnIndex,
                String header,
                String propertyName,
                String entitySetterMethod,
                boolean required,
                String description) {
            
            this.columnIndex = columnIndex;
            this.header = header;
            this.propertyName = propertyName;
            this.entitySetterMethod = entitySetterMethod;
            this.required = required;
            this.description = description;
        }

        public int getColumnIndex() {
            return columnIndex;
        }

        public String getHeader() {
            return header;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public String getEntitySetterMethod() {
            return entitySetterMethod;
        }

        public boolean isRequired() {
            return required;
        }

        public String getDescription() {
            return description;
        }
        
        public abstract InputHandler createInputHandlerInstance();

    }
    
    protected class StringColumnSpec extends ColumnSpec {
        
        private int maxLength;
        
        public StringColumnSpec(
                int columnIndex,
                String header,
                String propertyName,
                String entitySetterMethod,
                boolean required,
                String description,
                int maxLength) {
                    
            super(columnIndex, header, propertyName, entitySetterMethod, required, description);
            this.maxLength = maxLength;
        }

        public int getMaxLength() {
            return maxLength;
        }
        
        @Override
        public InputHandler createInputHandlerInstance() {
            return new StringInputHandler(
                    getColumnIndex(), 
                    getPropertyName(), 
                    getEntitySetterMethod(), 
                    getMaxLength());
        }
    }
    
    protected class BooleanColumnSpec extends ColumnSpec {
        
        public BooleanColumnSpec(int columnIndex, String header, String propertyName, String entitySetterMethod, boolean required, String description) {
            super(columnIndex, header, propertyName, entitySetterMethod, required, description);
        }
        
        @Override
        public InputHandler createInputHandlerInstance() {
            return new BooleanInputHandler(
                    getColumnIndex(), getPropertyName(), getEntitySetterMethod());
        }
    }
    
    protected class IdRefColumnSpec extends ColumnSpec {
        
        private CdbEntityController controller;
        private Class paramType;
        
        public IdRefColumnSpec(int columnIndex, String header, String propertyName, String entitySetterMethod, boolean required, String description, CdbEntityController controller, Class paramType) {
            super(columnIndex, header, propertyName, entitySetterMethod, required, description);
            this.controller = controller;
            this.paramType = paramType;
        }
        
        @Override
        public InputHandler createInputHandlerInstance() {
            return new IdRefInputHandler(
                    getColumnIndex(),
                    getPropertyName(),
                    getEntitySetterMethod(),
                    controller,
                    paramType);
        }
    }
    
    protected class IdOrNameRefColumnSpec extends ColumnSpec {
        
        private CdbEntityController controller;
        private Class paramType;
        private String domainNameFilter = null;
        
        public IdOrNameRefColumnSpec(int columnIndex, String header, String propertyName, String entitySetterMethod, boolean required, String description, CdbEntityController controller, Class paramType, String domainNameFilter) {
            super(columnIndex, header, propertyName, entitySetterMethod, required, description);
            this.controller = controller;
            this.paramType = paramType;
            this.domainNameFilter = domainNameFilter;
        }
        
        @Override
        public InputHandler createInputHandlerInstance() {
            return new IdOrNameRefInputHandler(
                    getColumnIndex(),
                    getPropertyName(),
                    getEntitySetterMethod(),
                    controller,
                    paramType,
                    domainNameFilter);
        }
    }
    
    public class OutputColumnModel implements Serializable {
        
        private int columnIndex;
        private String header = null;
        private String domainProperty = null;
        
        public OutputColumnModel(
                int columnIndex,
                String header, 
                String domainProperty) {
            
            this.columnIndex = columnIndex;
            this.header = header;
            this.domainProperty = domainProperty; // must have getter method
        }
        
        public int getColumnIndex() {
            return columnIndex;
        }

        public String getHeader() {
            return header;
        }

        public String getDomainProperty() {
            return domainProperty;
        }

    }
    
    public class InputColumnModel {
        
        protected int columnIndex;
        protected String name;
        protected String description = null;
        protected boolean required = false;
        
        public InputColumnModel(
                int columnIndex,
                String name,
                boolean required,
                String description) {
            
            this.columnIndex = columnIndex;
            this.name = name;
            this.description = description;
            this.required = required;
        }
        
        public int getColumnIndex() {
            return columnIndex;
        }
                
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }

        public boolean isRequired() {
            return this.required;
        }

    }
    
    public abstract class InputHandler {
        
        public abstract ValidInfo handleInput(
                Row row, 
                Map<Integer, String> cellValueMap, 
                Map<String, Object> rowMap);
        
        public ValidInfo updateEntity(
                Map<String, Object> rowMap, 
                EntityType entity) {
            return new ValidInfo(true, "");
        }
        
    }
    
    public abstract class SingleColumnInputHandler extends InputHandler {
        
        protected int columnIndex;
        
        public SingleColumnInputHandler(int columnIndex) {
            this.columnIndex = columnIndex;
        }
        
        public int getColumnIndex() {
            return columnIndex;
        }
    }
    
    public abstract class ColumnRangeInputHandler extends InputHandler {

        private int firstColumnIndex;
        private int lastColumnIndex;
        
        public ColumnRangeInputHandler(int firstColumnIndex, int lastColumnIndex) {
            this.firstColumnIndex = firstColumnIndex;
            this.lastColumnIndex = lastColumnIndex;
        }
        
        public int getFirstColumnIndex() {
            return firstColumnIndex;
        }

        public int getLastColumnIndex() {
            return lastColumnIndex;
        }
                
    }
    
    public abstract class SimpleInputHandler extends SingleColumnInputHandler {
        
        protected String propertyName = null;
        protected String setterMethod = null;

        public SimpleInputHandler(
                int columnIndex,
                String propertyName,
                String setterMethod) {
            super(columnIndex);
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
            
            String cellValue = cellValueMap.get(columnIndex);
                        
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
        public ValidInfo updateEntity(Map<String, Object> rowMap, EntityType entity) {
            
            boolean isValid = true;
            String validString = "";
            String methodLogName = "SimpleInputHandler::updateEntity() ";
            
            // get row dictionary value
            Object parsedValue = rowMap.get(getPropertyName());
            if (parsedValue != null) {
                try {
                    // use reflection to invoke setter method on entity instance
                    Method setterMethod;
                    Class paramType = getParamType();
                    setterMethod = entity.getClass().getMethod(getSetterMethod(), paramType);
                    setterMethod.invoke(entity, parsedValue);
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    isValid = false;
                    validString
                            = "Unable to invoke setter method: " + getSetterMethod()
                            + " for column: " + columnNameForIndex(columnIndex)
                            + " reason: " + ex.getClass().getName();
                    LOGGER.info(methodLogName + validString);
                    return new ValidInfo(isValid, validString);
                }
            }
            
            return new ValidInfo(isValid, validString);
        }
    }

    public class StringInputHandler extends SimpleInputHandler {

        protected int maxLength = 0;
        
        public StringInputHandler(
                int columnIndex,
                String propertyName,
                String setterMethod, 
                int maxLength) {
            super(columnIndex, propertyName, setterMethod);
            this.maxLength = maxLength;
        }

        public int getMaxLength() {
            return maxLength;
        }

        @Override
        public ParseInfo parseCellValue(String value) {
            
            boolean isValid = true;
            String validString = "";
            
            if ((getMaxLength() > 0) && (value.length() > getMaxLength())) {
                isValid = false;
                validString = appendToString(validString, 
                        "Value length exceeds " + getMaxLength() + 
                                " characters for column " + columnNameForIndex(columnIndex));
            }
            return new ParseInfo<>(value, isValid, validString);
        }
        
        @Override
        public Class getParamType() {
            return String.class;
        }
    }

    public class BooleanInputHandler extends SimpleInputHandler {

        public BooleanInputHandler(int columnIndex, String propertyName, String setterMethod) {
            super(columnIndex, propertyName, setterMethod);
        }

        @Override
        public ParseInfo parseCellValue(String stringValue) {
            
            Boolean parsedValue = null;
            boolean isValid = true;
            String validString = "";

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
                    validString = "unexpected boolean value: " + stringValue
                            + " for column: " + columnNameForIndex(columnIndex);
                }
            }

            return new ParseInfo<>(parsedValue, isValid, validString);
        }
        
        @Override
        public Class getParamType() {
            return Boolean.class;
        }
    }
    
    public abstract class RefInputHandler extends SimpleInputHandler {
        
        protected CdbEntityController controller;
        protected Class paramType;
        
        protected Map<Object, CdbEntity> objectIdMap = new HashMap<>();

        public RefInputHandler(
                int columnIndex, 
                String propertyName,
                String setterMethod, 
                CdbEntityController controller, 
                Class paramType) {
            super(columnIndex, propertyName, setterMethod);
            this.controller = controller;
            this.paramType = paramType;
        }
        
        @Override
        public Class getParamType() {
            return paramType;
        }
    }

    public class IdRefInputHandler extends RefInputHandler {
        
        public IdRefInputHandler(
                int columnIndex, 
                String propertyName,
                String setterMethod, 
                CdbEntityController controller, 
                Class paramType) {
            super(columnIndex, propertyName, setterMethod, controller, paramType);
        }

        @Override
        public ParseInfo parseCellValue(String strValue) {
            boolean isValid = true;
            String validString = "";
            CdbEntity objValue = null;
            if (strValue != null && strValue.length() > 0) {
                try {
                    int id = Integer.valueOf(strValue);
                    if (objectIdMap.containsKey(id)) {
                        objValue = objectIdMap.get(id);
                        LOGGER.debug("found object in cache with id: " + id);
                    } else {
                        objValue = controller.findById(id);
                        if (objValue == null) {
                            isValid = false;
                            validString
                                    = "Unable to find object for: "
                                    + columnNameForIndex(columnIndex)
                                    + " with id: " + strValue;
                        } else {
                            objectIdMap.put(objValue.getId(), objValue);
                        }
                    }
                } catch (NumberFormatException ex) {
                    isValid = false;
                    validString = "invalid id number format: " + strValue;
                }
            }
            return new ParseInfo<>(objValue, isValid, validString);
        }
    }

    public class IdOrNameRefInputHandler extends RefInputHandler {
        
        String domainNameFilter = null;
        
        public IdOrNameRefInputHandler(
                int columnIndex, 
                String propertyName,
                String setterMethod, 
                CdbEntityController controller, 
                Class paramType, 
                String domainNameFilter) {
            super(columnIndex, propertyName, setterMethod, controller, paramType);
            this.domainNameFilter = domainNameFilter;
        }

        @Override
        public ParseInfo parseCellValue(String strValue) {
            
            CdbEntity objValue = null;
            if ((strValue != null) && (!strValue.isEmpty())) {
                
                if (strValue.charAt(0) == '#') {
                    // process cell as name if first char is #
                    if (strValue.length() < 2) {
                        String msg = "invalid object name reference: " + strValue;
                        return new ParseInfo<>(objValue, false, msg);
                    }
                    String name = strValue.substring(1);
                    try {
                        objValue = controller.findUniqueByName(name, domainNameFilter);
                        if (objValue == null) {
                            String msg = "Unable to find object with name: " + name;
                            return new ParseInfo<>(objValue, false, msg);
                        } else {
                            // check cache for object so different references use same instance
                            int id = (Integer) objValue.getId();
                            if (objectIdMap.containsKey(id)) {
                                objValue = objectIdMap.get(id);
                                LOGGER.debug("found object in cache with id: " + id);
                            } else {
                                // add this instance to cache
                                objectIdMap.put(id, objValue);
                            }
                        }
                    } catch (CdbException ex) {
                        String msg = "Exception searching for object with name: " + name
                                + " reason: " + ex.getMessage();
                        return new ParseInfo<>(objValue, false, msg);
                    }

                } else {
                    // process cell as numeric
                    int id = 0;
                    try {
                        id = Integer.parseInt(strValue);
                        // check cache for object so different references use same instance
                        if (objectIdMap.containsKey(id)) {
                            objValue = objectIdMap.get(id);
                            LOGGER.debug("found object in cache with id: " + id);
                        } else {
                            objValue = controller.findById(id);
                            if (objValue == null) {
                                String msg = "Unable to find object with id: " + id;
                                return new ParseInfo<>(objValue, false, msg);
                            } else {
                                // add to cache
                                objectIdMap.put(id, objValue);
                            }
                        }
                    } catch (NumberFormatException ex) {
                        String msg = "invalid number format in id: " + strValue;
                        return new ParseInfo<>(objValue, false, msg);
                    }
                }
            }
            
            return new ParseInfo<>(objValue, true, "");
        }
    }
    
    public class InitializeInfo {
        public List<InputColumnModel> inputColumns;
        public List<InputHandler> inputHandlers;
        public List<OutputColumnModel> outputColumns;
        public ValidInfo validInfo;
        
        public InitializeInfo(
                List<InputColumnModel> inputColumns,
                List<InputHandler> inputHandlers,
                List<OutputColumnModel> outputColumns,
                ValidInfo validInfo) {
            
            this.inputColumns = inputColumns;
            this.inputHandlers = inputHandlers;
            this.outputColumns = outputColumns;
            this.validInfo = validInfo;            
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

    protected class ImportInfo {

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
    
    protected class CreateInfo {
        
        private EntityType entity;
        private ValidInfo validInfo;
        
        public CreateInfo(EntityType entity, ValidInfo validInfo) {
            this.entity = entity;
            this.validInfo = validInfo;
        }
        
        public CreateInfo(EntityType entity, boolean isValid, String validString) {
            this(entity, new ValidInfo(isValid, validString));
        }

        public EntityType getEntity() {
            return entity;
        }

        public ValidInfo getValidInfo() {
            return validInfo;
        }
        
    }

    private static final Logger LOGGER = LogManager.getLogger(ImportHelperBase.class.getName());

    protected static String isValidHeader = "Is Valid";
    protected static String isValidProperty = "isValidImportString";
    protected static String validStringHeader = "Valid String";
    protected static String validStringProperty = "validStringImport";

    protected List<EntityType> rows = new ArrayList<>();
    
    protected SortedMap<Integer, InputColumnModel> inputColumnMap = new TreeMap<>();
    
    protected List<InputHandler> inputHandlers = null;
    
    private SortedMap<Integer, OutputColumnModel> outputColumnMap = new TreeMap<>();
    
    protected byte[] templateExcelFile = null;
    protected boolean validInput = true;
    private String validationMessage = "";
    private String summaryMessage = "";
    protected TreeNode rootTreeNode = new DefaultTreeNode("Root", null);

    public ImportHelperBase() {
    }

    public List<EntityType> getRows() {
        return rows;
    }

    public List<InputHandler> getInputHandlers() {
        return inputHandlers;
    }
    
    public List<OutputColumnModel> getTableViewColumns() {
        List<OutputColumnModel> columns = new ArrayList<>();
        columns.addAll(outputColumnMap.values());
        return columns;
    }
    
    public boolean isValidInput() {
        return validInput;
    }
    
    public String getValidationMessage() {
        return validationMessage;
    }

    public String getSummaryMessage() {
        return summaryMessage;
    }

    public String getCompletionUrl() {
        return getCompletionUrlValue();
    }
    
    private InitializeInfo initialize(
            int actualColumnCount,
            Map<Integer, String> headerValueMap) {
        
        List<InputColumnModel> inputColumns = new ArrayList<>();
        List<InputHandler> inputHandlers = new ArrayList<>();
        List<OutputColumnModel> outputColumns = new ArrayList<>();
        
        List<ColumnSpec> columnSpecs = getColumnSpecs();        
        for (ColumnSpec spec : columnSpecs) {
            
            inputColumns.add(new InputColumnModel(
                    spec.getColumnIndex(),
                    spec.getHeader(),
                    spec.isRequired(),
                    spec.getDescription()));
            
            inputHandlers.add(spec.createInputHandlerInstance());
            
            outputColumns.add(new OutputColumnModel(
                    spec.getColumnIndex(),
                    spec.getHeader(), 
                    spec.getPropertyName()));
        }
        
        ValidInfo validInfo = initialize_(
                actualColumnCount, 
                headerValueMap, 
                inputColumns, 
                inputHandlers, 
                outputColumns);
        
        return new InitializeInfo(inputColumns, inputHandlers, outputColumns, validInfo);
    }
    
    /**
     * Allow subclass to do optional custom initialization. 
     */
    protected ValidInfo initialize_(
            int actualColumnCount,
            Map<Integer, String> headerValueMap,
            List<InputColumnModel> inputColumns,
            List<InputHandler> inputHandlers,
            List<OutputColumnModel> outputColumns) {
        
        return new ValidInfo(true, "");
    }

    private ValidInfo initializeHelper(
            int actualColumnCount,
            Map<Integer, String> headerValueMap) {
        
        InitializeInfo initInfo = initialize(actualColumnCount, headerValueMap);
        
        ValidInfo initValidInfo = initInfo.validInfo;
        if (initValidInfo.isValid()) {
            initializeInputColumns(initInfo.inputColumns);
            initializeInputHandlers(initInfo.inputHandlers);
            initializeViewColumns(initInfo.outputColumns);
        }
        
        return initValidInfo;
    }
    
    private void initializeInputColumns(List<InputColumnModel> columns) {
        for (InputColumnModel col : columns) {
            inputColumnMap.put(col.getColumnIndex(), col);
        }
    }
    
    protected void initializeInputHandlers(List<InputHandler> specs) {        
        inputHandlers = specs;
    }
    
    protected void initializeViewColumns(List<OutputColumnModel> columns) {
        
        // these are special columns for displaying validation info for each row
        int numColumns = columns.size();
        columns.add(new OutputColumnModel(numColumns, isValidHeader, isValidProperty));
        columns.add(new OutputColumnModel(numColumns + 1, validStringHeader, validStringProperty));  
        
        for (OutputColumnModel col : columns) {
            outputColumnMap.put(col.getColumnIndex(), col);
        }
    }

    /**
     * Returns the list of input columns for the download empty template file
     * feature.
     */
    protected List<InputColumnModel> getTemplateColumns() {

        List<InputColumnModel> columns = new ArrayList<>();
        
        List<ColumnSpec> columnSpecs = getColumnSpecs();
        for (ColumnSpec spec : columnSpecs) {
            columns.add(new InputColumnModel(
                    spec.getColumnIndex(),
                    spec.getHeader(),
                    spec.isRequired(),
                    spec.getDescription()));
        }
        
        return columns;
    }
    
    protected void reset_() {
        // allow subclass to reset, by default do nothing
    }

    public void reset() {
        rows.clear();
        validInput = true;
        validationMessage = "";
        rootTreeNode = new DefaultTreeNode("Root", null);

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
    
    protected String columnNameForIndex(int index) {
        InputColumnModel col = inputColumnMap.get(index);
        if (col != null) {
            return col.getName();
        } else {
            return "";
        }
    }
    
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
        List<InputColumnModel> columns = getTemplateColumns();
        for (InputColumnModel col : columns) {
            
            Cell headerCell = headerRow.createCell(col.getColumnIndex());
            headerCell.setCellValue(col.getName());
            
            // set up box for comment/description
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
            LOGGER.error("buildTemplateExcelFile() " + ex);
        }
    }

    protected boolean readXlsFileData(UploadedFile f) {

        InputStream inputStream;
        HSSFWorkbook workbook = null;
        try {
            inputStream = f.getInputStream();
            workbook = new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            return false;
        }

        HSSFSheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();

        parseSheet(rowIterator);

        return true;
    }

    protected boolean readXlsxFileData(UploadedFile f, String sheetName) {

        InputStream inputStream;
        XSSFWorkbook workbook = null;
        try {
            inputStream = f.getInputStream();
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            return false;
        }

        XSSFSheet sheet = workbook.getSheet(sheetName);
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
    
    public List<String> getSheetNames(UploadedFile f) {
        
        List<String> sheetNames = new ArrayList<>();
        
        InputStream inputStream;
        XSSFWorkbook workbook = null;
        try {
            inputStream = f.getInputStream();
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            LOGGER.info("error opening excel file: " + e);
            return sheetNames;
        }
        
        int numSheets = workbook.getNumberOfSheets();
        for (int i = 0 ; i < numSheets ; i++) {
            String sheetName = workbook.getSheetName(i);
            sheetNames.add(sheetName);
            System.out.println(sheetName);
        }
        
        return sheetNames;
    }
    
    public ValidInfo validateSheet(UploadedFile f, String sheetName) {
        
        boolean isValid = true;
        String validString = "";
        String logMethodName = "validateSheet() ";
        
        InputStream inputStream;
        XSSFWorkbook workbook = null;
        try {
            inputStream = f.getInputStream();
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            isValid = false;
            validString = "error opening excel file: " + e;
            LOGGER.info(logMethodName + validString);
            return new ValidInfo(isValid, validString);
        }        
        
        XSSFSheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            isValid = false;
            validString = "no sheet found with name: " + sheetName;
            LOGGER.info(logMethodName + validString);
            return new ValidInfo(isValid, validString);
        }
        
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            isValid = false;
            validString = "no header row found in file";
            LOGGER.info(logMethodName + validString);
            return new ValidInfo(isValid, validString);            
        }          
        
        ValidInfo headerValidInfo = parseHeader(headerRow);
        if (!headerValidInfo.isValid()) {
            isValid = false;
            validString = headerValidInfo.getValidString();
            LOGGER.info(logMethodName + validString);
            return new ValidInfo(isValid, validString);
        }      
        
        return new ValidInfo(isValid, validString);
    }

    protected void parseSheet(Iterator<Row> rowIterator) {

        int rowCount = -1;
        int entityNum = 0;
        int dupCount = 0;
        int invalidCount = 0;
        String dupString = "";
        
        while (rowIterator.hasNext()) {

            rowCount = rowCount + 1;
            Row row = rowIterator.next();

            if (rowCount == 0) {
                // parse and validate header row
                ValidInfo headerValidInfo = parseHeader(row);
                
                if (!headerValidInfo.isValid()) {
                    // don't parse the spreadsheet if the format is invalid
                    validInput = false;
                    validationMessage = 
                            "Warning: " + headerValidInfo.getValidString() + 
                            ". Please make sure spreadsheet format is correct and enter values in all header rows before proceeding";
                    return;
                }
                
            } else {
                // parse spreadsheet data row
                ValidInfo rowValidInfo = parseRow(row);
                if (!rowValidInfo.isValid()) {
                    validInput = false;
                    invalidCount = invalidCount + 1;
                }
                if (rowValidInfo.isDuplicate()) {
                    dupCount = dupCount + 1;
                    dupString = appendToString(dupString, rowValidInfo.getValidString());
                }
            }
        }
        
        if (dupCount > 0) {
            validationMessage = appendToString(
                    validationMessage, 
                    "Removed " + dupCount + 
                            " rows that already exist in database: (" + dupString + ")");
        }
        if (rows.size() == 0) {
            // nothing to import, this will disable the "next" button
            validInput = false;
            validationMessage = appendToString(
                    validationMessage, 
                    "Nothing to import");
        }
        if (validInput) {
            String summaryDetails = rows.size() + " new items ";
            String customSummaryDetails = getCustomSummaryDetails();
            if (!customSummaryDetails.isEmpty()) {
                summaryDetails = customSummaryDetails;
            }
            summaryMessage = 
                    "Press 'Next Step' to complete the import process. " +
                    "This will create " + 
                    summaryDetails +
                    " displayed in table above.";
        } else {
            validationMessage = appendToString(
                    validationMessage, 
                    "Spreadsheet includes " + invalidCount + " invalid row(s).");
            summaryMessage = 
                    "Press 'Cancel' to terminate the import process and fix " +
                    "problems with import spreadsheet." +
                    " No new items will be created.";
        }
    }
    
    /**
     * Allows subclass to customize summary details message.
     */
    protected String getCustomSummaryDetails() {
        return "";
    }
    
    /**
     * Checks that the number of values present in the name row matches the
     * expected number of columns.
     * @param row
     * @return 
     */
    private ValidInfo parseHeader(Row row) {
        
        boolean isValid = true;
        String validMessage = "";
        
        // get number of actual columns and read header row 
        // into map (columnIndex -> cellValue)
        int actualColumns = row.getLastCellNum();
        Map<Integer, String> headerValueMap = new HashMap<>();
        for (int colIndex = 0 ; colIndex < actualColumns ; ++colIndex) {
            Cell cell = row.getCell(colIndex);
            String cellValue = parseStringCell(cell);
            headerValueMap.put(colIndex, cellValue);
        }
        
        // initialize helper data structures
        ValidInfo initInfo = initializeHelper(actualColumns, headerValueMap);
        
        if (!initInfo.isValid()) {
            isValid = false;
            validMessage = initInfo.getValidString();
            
        } else {
            // check actual number of columns against expected number
            int maxColIndex = Collections.max(inputColumnMap.keySet());
            int expectedColumns = maxColIndex + 1;
            if (expectedColumns != actualColumns) {
                isValid = false;
                validMessage
                        = "header row (" + actualColumns
                        + ") does not contain expected number of columns ("
                        + expectedColumns + ")";
            }
        }
        
        return new ValidInfo(isValid, validMessage);
    }
    
    private ValidInfo parseRow(Row row) {

        boolean isValid = true;
        String validString = "";
        boolean isDuplicate = false;
        
        // parse each column value into a map (cellIndex -> cellValue)        
        int colIndex;
        Map<Integer, String> cellValueMap = new HashMap<>();
        for (InputColumnModel col : inputColumnMap.values()) {
            colIndex = col.getColumnIndex();
            Cell cell = row.getCell(colIndex);
            String cellValue = parseStringCell(cell);
            cellValueMap.put(colIndex, cellValue);
            
            // check that value is present for required column
            if (col.isRequired() && ((cellValue == null) || (cellValue.isEmpty()))) {
                isValid = false;
                validString = appendToString(validString, 
                        "Required value missing for " + columnNameForIndex(col.getColumnIndex()));
            }
        }
        
        // skip blank rows
        if (isBlankRow(cellValueMap)) {
            return new ValidInfo(true, "");
        }

        // invoke each input handler to populate row dictionary (String key -> object)
        Map<String, Object> rowDict = new HashMap<>();
        for (InputHandler handler : inputHandlers) {
            ValidInfo validInfo = handler.handleInput(row, cellValueMap, rowDict);
            if (!validInfo.isValid()) {
                validString = appendToString(validString, validInfo.getValidString());
                isValid = false;
            }
        }
        
        CreateInfo createInfo = createEntityInstance(rowDict);
        EntityType newEntity = createInfo.getEntity();
        ValidInfo createValidInfo = createInfo.getValidInfo();
        if (!createValidInfo.isValid()) {
            validString = appendToString(validString, createValidInfo.getValidString());
            isValid = false;
        }
        
        // invoke each input handler to update the entity with row dictionary values
        for (InputHandler handler : inputHandlers) {
            ValidInfo validInfo = handler.updateEntity(rowDict, newEntity);
            if (!validInfo.isValid()) {
                validString = appendToString(validString, validInfo.getValidString());
                isValid = false;
            }
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
    
    protected boolean isBlankRow(Map<Integer, String> cellValues) {
        for (String value : cellValues.values()) {
            if ((value != null) && (!value.isEmpty())) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Provides callback for helper subclass to handle the result of a parsed row
     * at the entity level.  The subclass can update one field of the entity based
     * on the value in some other field, for example.  Default behavior is to
     * do nothing, subclasses override to customize.
     * @param e
     * @return 
     */
    protected ValidInfo postParseRow(EntityType e) {
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
    
    /**
     * Specifies whether the subclass will provide a tree view. Returns true if
     * there are children of the root tree node, meaning that the subclass built
     * the tree model during parsing.
     */
    public boolean hasTreeView() {
        return getRootTreeNode().getChildCount() > 0;
    }
    
    public TreeNode getRootTreeNode() {
        return rootTreeNode;
    }
    
    protected abstract List<ColumnSpec> getColumnSpecs();
    
    protected abstract String getCompletionUrlValue();

    public abstract EntityControllerType getEntityController();
    
    public abstract String getTemplateFilename();
    
    protected abstract CreateInfo createEntityInstance(Map<String, Object> rowMap);
}
