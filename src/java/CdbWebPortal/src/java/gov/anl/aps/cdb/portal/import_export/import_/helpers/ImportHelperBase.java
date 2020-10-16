/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.controllers.ItemCategoryController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainLocationController;
import gov.anl.aps.cdb.portal.controllers.ItemProjectController;
import gov.anl.aps.cdb.portal.controllers.ItemTypeController;
import gov.anl.aps.cdb.portal.controllers.UserGroupController;
import gov.anl.aps.cdb.portal.controllers.UserInfoController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnSpecInitInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ImportInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.InitializeInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.InputColumnModel;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.OutputColumnModel;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefListColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
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

    private static final Logger LOGGER = LogManager.getLogger(ImportHelperBase.class.getName());

    private static final String HEADER_IS_VALID = "Is Valid";
    private static final String PROPERTY_IS_VALID = "isValidImportString";
    private static final String HEADER_VALID_STRING = "Valid String";
    private static final String PROPERTY_VALID_STRING = "validStringImport";
    
    protected static final String KEY_USER = "ownerUserName";
    protected static final String KEY_GROUP = "ownerUserGroupName";
    
    private static final String INDICATOR_COMMENT = "//";

    protected List<EntityType> rows = new ArrayList<>();
    
    protected SortedMap<Integer, InputColumnModel> inputColumnMap = new TreeMap<>();
    
    protected List<InputHandler> inputHandlers = null;
    
    private List<OutputColumnModel> outputColumns = new ArrayList<>();
    
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

    public List<OutputColumnModel> getTableViewColumns() {
        return outputColumns;
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

    private InitializeInfo initialize(
            int actualColumnCount,
            Map<Integer, String> headerValueMap) {
        
        List<InputColumnModel> inputColumns = new ArrayList<>();
        List<InputHandler> handlers = new ArrayList<>();
        List<OutputColumnModel> outputColumns = new ArrayList<>();
        
        List<ColumnSpec> columnSpecs = getColumnSpecs();   
        int colIndex = 0;
        ValidInfo validInfo = new ValidInfo(true, "");
        for (ColumnSpec spec : columnSpecs) {
            ColumnSpecInitInfo initInfo = spec.initialize(
                    colIndex, 
                    headerValueMap, 
                    inputColumns, 
                    handlers,  
                    outputColumns);    
            
            if (!initInfo.getValidInfo().isValid()) {
                validInfo = initInfo.getValidInfo();
                break;
            }
            
            colIndex = colIndex + initInfo.getNumColumns();   
        }
        
        return new InitializeInfo(inputColumns, handlers, outputColumns, validInfo);
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
        columns.add(new OutputColumnModel(HEADER_IS_VALID, PROPERTY_IS_VALID));
        columns.add(new OutputColumnModel(HEADER_VALID_STRING, PROPERTY_VALID_STRING));
        outputColumns = columns;
    }

    /**
     * Returns the list of input columns for the download empty template file
     * feature.
     */
    protected List<InputColumnModel> getTemplateColumns() {

        List<InputColumnModel> columns = new ArrayList<>();
        
        List<ColumnSpec> columnSpecs = getColumnSpecs();
        int columnIndex = 0;
        for (ColumnSpec spec : columnSpecs) {
            int numColumns = spec.getInputTemplateColumns(columnIndex, columns);
            columnIndex = columnIndex + numColumns;
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

        String parsedValue;

        if (cell == null) {
            parsedValue = "";
        } else {
            cell.setCellType(CellType.STRING);
            parsedValue = cell.getStringCellValue().trim();
        }

        return parsedValue;
    }

    public StreamedContent getTemplateExcelFile() {
        if (templateExcelFile == null) {
            buildTemplateExcelFile();
        }
        InputStream inStream = new ByteArrayInputStream(templateExcelFile);
        return new DefaultStreamedContent(inStream, "xlsx", getTemplateFilename() + ".xlsx");
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
        
        // add some documentation
        Row blankRow = sheet.createRow(1);
        Row commentRow = sheet.createRow(2);
        Cell commentCell = commentRow.createCell(0);
        commentCell.setCellValue("// Comment rows must begin with '//'.  Blank Rows are allowed.  Most ID columns also accept names or list of names, but names must be unique and cell value must be prefixed with '#' character.");

        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
            wb.write(outStream);
            templateExcelFile = outStream.toByteArray();
        } catch (IOException ex) {
            LOGGER.error("buildTemplateExcelFile() " + ex);
        }
    }

    public List<String> getSheetNames(UploadedFile f) {
        
        List<String> sheetNames = new ArrayList<>();
        
        InputStream inputStream;
        XSSFWorkbook workbook;
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
        }
        
        return sheetNames;
    }
    
//    /**
//     * Reads Excel "xls" file.  This is not currently used, but I added it for
//     * documentation in case we need to read that format.
//     */
//    protected boolean readXlsFileData(UploadedFile f) {
//
//        InputStream inputStream;
//        HSSFWorkbook workbook;
//        try {
//            inputStream = f.getInputStream();
//            workbook = new HSSFWorkbook(inputStream);
//        } catch (IOException e) {
//            return false;
//        }
//
//        HSSFSheet sheet = workbook.getSheetAt(0);
//
//        Iterator<Row> rowIterator = sheet.iterator();
//
//        parseSheet(rowIterator);
//
//        return true;
//    }

    public boolean readXlsxFileData(
            UploadedFile f, 
            String sheetName,
            int rowNumberHeader,
            int rowNumberFirstData,
            int rowNumberLastData) {

        InputStream inputStream;
        XSSFWorkbook workbook;
        try {
            inputStream = f.getInputStream();
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            validInput = false;
            validationMessage = "Unable to open file " + f.getFileName();
            summaryMessage
                    = "Press 'Cancel' to terminate the import process and fix "
                    + "problems with spreadsheet file."
                    + " No new items will be created.";
            return false;
        }

        XSSFSheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            validInput = false;
            validationMessage = "Unable to open specified sheet " + sheetName;
            summaryMessage
                    = "Press 'Cancel' to terminate the import process and fix "
                    + "problems with spreadsheet file."
                    + " No new items will be created.";
            return false;
        }

        // validate specified row numbers, change user specified 1-based values to 0-based values for api, and set default values if not specified
        boolean validOptions = true;
        String optionMessage = "";
        
        if (rowNumberHeader == -1) {
            rowNumberHeader = 0;
        } else if ((rowNumberHeader < 1)
                || ((rowNumberFirstData != -1) && (rowNumberHeader >= rowNumberFirstData))
                || ((rowNumberLastData != -1) && (rowNumberHeader >= rowNumberLastData))) {
            validOptions = false;
            optionMessage = optionMessage
                    + "Header row must be greater than 0, and less than both data rows. ";
        } else {
                rowNumberHeader = rowNumberHeader - 1;
        }
        
        if (rowNumberFirstData == -1) {
            rowNumberFirstData = 1;
        } else  if ((rowNumberFirstData < 2) || 
                    ((rowNumberHeader != -1) && (rowNumberFirstData <= rowNumberHeader)) || 
                    ((rowNumberLastData != -1) && (rowNumberFirstData > rowNumberLastData))) {
            validOptions = false;
            optionMessage = optionMessage
                    + "First data row must be greater than 1, greater than header row, and less than or equal to last data row. ";
        } else {
            rowNumberFirstData = rowNumberFirstData - 1;
        }
        
        if (rowNumberLastData == -1) {
            rowNumberLastData = sheet.getLastRowNum();
        } else if ((rowNumberLastData < 2) || 
                    ((rowNumberHeader != -1) && (rowNumberLastData <= rowNumberHeader)) || 
                    ((rowNumberFirstData != -1) && (rowNumberFirstData > rowNumberLastData))) {
            validOptions = false;
            optionMessage = optionMessage
                    + "Last data row must be greater than 1, greater than header row, and greater than or equal to first data row. ";
        } else {
            rowNumberLastData = rowNumberLastData - 1;
        }
        
        if (!validOptions) {
            validInput = false;
            validationMessage = "Invalid row number options. " + optionMessage;
            summaryMessage
                    = "Press 'Cancel' to terminate the import process and fix "
                    + "problems with row number options."
                    + " No new items will be created.";
            return false;
        }
        
        parseSheet(sheet, rowNumberHeader, rowNumberFirstData, rowNumberLastData);
        return true;
    }
    
    protected void parseSheet(
            XSSFSheet sheet,
            int rowNumberHeader,
            int rowNumberFirstData,
            int rowNumberLastData) {

        int entityNum = 0;
        int dupCount = 0;
        int invalidCount = 0;
        String dupString = "";
        
        System.out.println("row numbers: " + rowNumberHeader + " " + rowNumberFirstData + " " + rowNumberLastData);
                    
        // parse / validate header row  
        Row headerRow = sheet.getRow(rowNumberHeader);
        ValidInfo headerValidInfo = parseHeader(headerRow);
        if (!headerValidInfo.isValid()) {
            validInput = false;
            validationMessage = headerValidInfo.getValidString();
            summaryMessage
                    = "Press 'Cancel' to terminate the import process and fix "
                    + "problems with import spreadsheet."
                    + " No new items will be created.";
            return;
        }
            
        // parse spreadsheet data rows
        for (int rowNumber = rowNumberFirstData ; rowNumber <= rowNumberLastData ; rowNumber ++) {
            Row row = sheet.getRow(rowNumber);
            if (row != null) {
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
        if (rows.isEmpty()) {
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
            if (actualColumns < 0) {
                actualColumns = 0;
            }
            if (expectedColumns != actualColumns) {
                isValid = false;
                validMessage
                        = "Actual number of header row columns (" + actualColumns
                        + ") does not match expected number of columns ("
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
        
        // skip blank and comment rows
        if (isBlankRow(cellValueMap) || isCommentRow(cellValueMap)) {
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
        EntityType newEntity = (EntityType) createInfo.getEntity();
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
                if (ex.getErrorMessage().startsWith("Uniqueness check not implemented by controller")) {
                    // ignore this?
                } else if (ignoreDuplicates()) {
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
    
    protected boolean isCommentRow(Map<Integer, String> cellValues) {
        String value = cellValues.get(0);
        if ((value != null) && (!value.isEmpty())) {
            if (value.trim().startsWith(INDICATOR_COMMENT)) {
                return true;
            }
        }
        return false;
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

        String message;
        try {
            controller.createList(rows);
            message = "Import succeeded, created " + rows.size() + " instances";
        } catch (CdbException | RuntimeException ex) {
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
    
    public abstract EntityControllerType getEntityController();
    
    public abstract String getTemplateFilename();
    
    protected abstract CreateInfo createEntityInstance(Map<String, Object> rowMap);
    
    public IdOrNameRefColumnSpec ownerUserColumnSpec() {
        return new IdOrNameRefColumnSpec("Owner User", KEY_USER, "setOwnerUser", false, "ID or name of CDB owner user. Name must be unique and prefixed with '#'.", UserInfoController.getInstance(), UserInfo.class, "");
    }
    
    public IdOrNameRefColumnSpec ownerGroupColumnSpec() {
        return new IdOrNameRefColumnSpec("Owner Group", KEY_GROUP, "setOwnerUserGroup", false, "ID or name of CDB owner user group. Name must be unique and prefixed with '#'.", UserGroupController.getInstance(), UserGroup.class, "");
    }
    
    public IdOrNameRefListColumnSpec projectListColumnSpec() {
        return new IdOrNameRefListColumnSpec("Project", "itemProjectString", "setItemProjectList", true, "Comma-separated list of IDs of CDB project(s). Name must be unique and prefixed with '#'.", ItemProjectController.getInstance(), List.class, "");
    }
    
    public IdOrNameRefListColumnSpec technicalSystemListColumnSpec(String domainName) {
        return new IdOrNameRefListColumnSpec("Technical System", "itemCategoryString", "setItemCategoryList", false, "Numeric ID of CDB technical system. Name must be unique and prefixed with '#'.", ItemCategoryController.getInstance(), List.class, domainName);
    }
    
    public IdOrNameRefListColumnSpec functionListColumnSpec(String domainName) {
        return new IdOrNameRefListColumnSpec("Function", "itemTypeString", "setItemTypeList", false, "Numeric ID of CDB technical system. Name must be unique and prefixed with '#'.", ItemTypeController.getInstance(), List.class, domainName);
    }
    
    public IdOrNameRefColumnSpec locationColumnSpec() {
        return new IdOrNameRefColumnSpec("Location", "importLocationItemString", "setImportLocationItem", false, "Item location.", ItemDomainLocationController.getInstance(), ItemDomainLocation.class, "");
    }
    
    public StringColumnSpec locationDetailsColumnSpec() {
        return new StringColumnSpec("Location Details", "locationDetails", "setLocationDetails", false, "Location details for item.", 256);
    }
}
