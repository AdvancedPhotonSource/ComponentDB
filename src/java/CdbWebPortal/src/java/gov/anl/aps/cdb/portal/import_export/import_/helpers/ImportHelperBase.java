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
import gov.anl.aps.cdb.portal.import_export.export.objects.ColumnValueResult;
import gov.anl.aps.cdb.portal.import_export.export.objects.ExportColumnData;
import gov.anl.aps.cdb.portal.import_export.export.objects.FieldValueDifference;
import gov.anl.aps.cdb.portal.import_export.export.objects.FieldValueDifferenceMap;
import gov.anl.aps.cdb.portal.import_export.export.objects.FieldValueMap;
import gov.anl.aps.cdb.portal.import_export.export.objects.FieldValueMapResult;
import gov.anl.aps.cdb.portal.import_export.export.objects.GenerateExportResult;
import gov.anl.aps.cdb.portal.import_export.export.objects.HandleOutputResult;
import gov.anl.aps.cdb.portal.import_export.export.objects.handlers.OutputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnSpecInitInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.HelperWizardOption;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ImportInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ImportMode;
import gov.anl.aps.cdb.portal.import_export.import_.objects.InitializeInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.InputColumnModel;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.OutputColumnModel;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.BooleanColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefListColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IntegerColumnSpec;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    
    private static final String MODE_CREATE = "create";
    private static final String MODE_UPDATE = "update";
    private static final String MODE_DELETE = "delete";
    private static final String MODE_COMPARE = "compare";

    private static final String HEADER_IS_VALID = "Is Valid";
    private static final String PROPERTY_IS_VALID = "isValidImportString";
    private static final String HEADER_VALID_STRING = "Validation Info";
    private static final String PROPERTY_VALID_STRING = "validStringImport";
    private static final String HEADER_DIFFS = "Differences";
    private static final String PROPERTY_DIFFS = "importDiffs";
    private static final String HEADER_ORIG = "Unchanged";
    private static final String PROPERTY_ORIG = "importUnchanged";
    
    protected static final String KEY_USER = "ownerUserName";
    protected static final String KEY_GROUP = "ownerUserGroupName";
    protected static final String KEY_EXISTING_ITEM_ID = "importExistingItemId";
    protected static final String KEY_DELETE_EXISTING_ITEM = "importDeleteExistingItem";
    
    
    private static final String INDICATOR_COMMENT = "//";

    protected List<EntityType> rows = new ArrayList<>();
    private List<CdbEntity> exportEntityList;
    
    protected ImportMode importMode;
    
    protected SortedMap<Integer, InputColumnModel> inputColumnMap = new TreeMap<>();
    
    protected List<InputHandler> inputHandlers = null;
    
    private List<OutputColumnModel> outputColumns = new ArrayList<>();
    
    protected byte[] templateExcelFile = null;
    protected boolean validInput = true;
    private String validationMessage = "";
    private String summaryMessage = "";
    protected TreeNode rootTreeNode = new DefaultTreeNode("Root", null);
    private int numExpectedColumns = 0;
    private List<HelperWizardOption> wizardOptions = null;

    public ImportHelperBase() {
    }

    public List<EntityType> getRows() {
        return rows;
    }

    public List<CdbEntity> getExportEntityList() {
        return exportEntityList;
    }

    public void setExportEntityList(List<CdbEntity> exportEntityList) {
        this.exportEntityList = exportEntityList;
    }
    
    public ImportMode getImportMode() {
        if (importMode == null) {
            importMode  = ImportMode.CREATE;
        }
        return importMode;
    }
    
    public void setImportMode(String importModeString) {
        if (importModeString.equals(MODE_CREATE)) {
            importMode = ImportMode.CREATE;
        } else if (importModeString.equals(MODE_UPDATE)) {
            importMode = ImportMode.UPDATE;
        } else if (importModeString.equals(MODE_DELETE)) {
            importMode = ImportMode.DELETE;
        } else if (importModeString.equals(MODE_COMPARE)) {
            importMode = ImportMode.COMPARE;
        }
    }
    
    public List<HelperWizardOption> getWizardOptions() {
        if (wizardOptions == null) {
            wizardOptions = new ArrayList<>();
            List<HelperWizardOption> helperOptions = initializeWizardOptions();
            for (HelperWizardOption option : helperOptions) {
                if (option.getMode() == getImportMode()) {
                    wizardOptions.add(option);
                }
            }
        }
        return wizardOptions;
    }
    
    /**
     * Initializes list of wizard options, overridden by subclasses to customize. 
     */
    protected List<HelperWizardOption> initializeWizardOptions() {
        return new ArrayList<>();
    }

    /**
     * Validates wizard options.  Overridden by subclasses to customize.
     */
    public ValidInfo validateWizardOptions() {
        return new ValidInfo(true, "");
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
                    headerValueMap);  
            
            if (!initInfo.getValidInfo().isValid()) {
                validInfo = initInfo.getValidInfo();
                break;
            } else {
                inputColumns.addAll(initInfo.getInputColumns());
                handlers.addAll(initInfo.getInputHandlers());
                
                // set validation table columns depending on  mode
                if (spec.isUsedForMode(getImportMode())) {
                    outputColumns.addAll(initInfo.getOutputColumns());
                }                
            }
            
            colIndex = colIndex + initInfo.getNumColumns();   
        }
        
        numExpectedColumns = colIndex;
        
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
            initializeValidationTableColumns(initInfo.outputColumns);
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
    
    protected void initializeValidationTableColumns(List<OutputColumnModel> columns) {        
        // these are special columns for displaying validation info for each row
        columns.add(new OutputColumnModel(HEADER_IS_VALID, PROPERTY_IS_VALID));
        columns.add(new OutputColumnModel(HEADER_VALID_STRING, PROPERTY_VALID_STRING));
        if (getImportMode() == ImportMode.COMPARE) {
            columns.add(new OutputColumnModel(HEADER_ORIG, PROPERTY_ORIG));
        }
        if ((getImportMode() == ImportMode.UPDATE) 
                || (getImportMode() == ImportMode.DELETE) 
                || (getImportMode() == ImportMode.COMPARE)) {
            columns.add(new OutputColumnModel(HEADER_DIFFS, PROPERTY_DIFFS));
        }
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
            parsedValue = null;
        } else {
            cell.setCellType(CellType.STRING);
            parsedValue = cell.getStringCellValue().trim();
        }

        return parsedValue;
    }

    private String getTemplateExcelFilename() {
        return getFilenameBase() + " Template";
    }
    
    public StreamedContent getTemplateExcelFile() {
        if (templateExcelFile == null) {
            buildTemplateExcelFile();
        }
        InputStream inStream = new ByteArrayInputStream(templateExcelFile);
        return new DefaultStreamedContent(inStream, "xlsx", getTemplateExcelFilename() + ".xlsx");
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

    public GenerateExportResult generateExportFile() {
        
        boolean isValid = true;
        String validString = "";
        
        // get export content via output handlers for each column
        List<ExportColumnData> exportContent = new ArrayList<>();
        List<CdbEntity> entities = getExportEntityList();
        for (ColumnSpec spec : getColumnSpecs()) {
            OutputHandler handler = spec.getOutputHandler();
            if (handler == null) {                
                ValidInfo validInfo = new ValidInfo(false, "Unexpected error, no output handler for column: " + spec.getHeader());
                return new GenerateExportResult(validInfo, null);
            }
            
            HandleOutputResult handleOutputResult = handler.handleOutput(entities);
            if (!handleOutputResult.getValidInfo().isValid()) {                
                ValidInfo validInfo = new ValidInfo(false, handleOutputResult.getValidInfo().getValidString());
                return new GenerateExportResult(validInfo, null);
            }
            
            if ((handleOutputResult.getColumnData() == null) || 
                    (handleOutputResult.getColumnData().isEmpty())) {
                
                ValidInfo validInfo = new ValidInfo(false, "Unexpected error, no column data for column: " + spec.getHeader());
                return new GenerateExportResult(validInfo, null);
            }
            exportContent.addAll(handleOutputResult.getColumnData());
        }
        
        // create excel workbook
        Workbook wb = new XSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet("export");
        Drawing drawing = sheet.createDrawingPatriarch();
        
        // create header row content
        int rowIndex = 0;
        int colIndex = 0;
        Row headerRow = sheet.createRow(rowIndex);
        for (ExportColumnData columnData : exportContent) {
            
            Cell headerCell = headerRow.createCell(colIndex);
            headerCell.setCellValue(columnData.getColumnName());
            
            // set up box for comment/description
            ClientAnchor anchor = createHelper.createClientAnchor();
            anchor.setCol1(headerCell.getColumnIndex());
            anchor.setCol2(headerCell.getColumnIndex() + 2);
            anchor.setRow1(headerRow.getRowNum());
            anchor.setRow2(headerRow.getRowNum() + 3);
            
            Comment headerComment = drawing.createCellComment(anchor);
            RichTextString str = createHelper.createRichTextString(columnData.getDescription());
            headerComment.setString(str);
            headerCell.setCellComment(headerComment);
            
            colIndex = colIndex + 1;
        }
        
        // create data row content, one column at a time
        colIndex = 0;
        for (ExportColumnData columnData : exportContent) {
            rowIndex = 1;
            for (String columnValue : columnData.getColumnValues()) {
                Row dataRow = sheet.getRow(rowIndex);
                if (dataRow == null) {
                    dataRow = sheet.createRow(rowIndex);
                }
                Cell dataCell = dataRow.createCell(colIndex);
                dataCell.setCellValue(columnValue);
                rowIndex = rowIndex + 1;
            }
            colIndex = colIndex + 1;
        }
        
        // create byte array containing excel binary file data
        byte[] exportFile = null;
        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
            wb.write(outStream);
            exportFile = outStream.toByteArray();
        } catch (IOException ex) {
            LOGGER.error("generateExportFile() " + ex);
        }
        
        // create streamed content from byte array
        StreamedContent content = null;
        if (exportFile != null) {
            InputStream inStream = new ByteArrayInputStream(exportFile);
            content = new DefaultStreamedContent(inStream, "xlsx", getExportFilename() + ".xlsx");
        } else {
            isValid = false;
            validString = "Unexpected error creating Excel file content";
        }
        
        ValidInfo validInfo = new ValidInfo(isValid, validString);
        return new GenerateExportResult(validInfo, content);
    }

    private FieldValueMapResult getFieldValueMap(EntityType entity) {
        
        boolean isValid = true;
        String validString = "";
        
        // get field values via output handlers for each column
        FieldValueMap valueMap = new FieldValueMap();
        for (ColumnSpec spec : getColumnSpecs()) {
            
            // skip columns not used for update mode
            if (!spec.isUsedForMode(ImportMode.UPDATE)) {
                continue;
            }
            
            OutputHandler handler = spec.getOutputHandler();
            if (handler == null) {                
                ValidInfo validInfo = new ValidInfo(false, "Unexpected error, no output handler for column: " + spec.getHeader());
                return new FieldValueMapResult(validInfo, valueMap);
            }
            
            ColumnValueResult columnValueResult = handler.handleOutput(entity);
            if (!columnValueResult.getValidInfo().isValid()) {
                return new FieldValueMapResult(columnValueResult.getValidInfo(), valueMap);
            }
            
            valueMap.put(spec.getHeader(), columnValueResult.getColumnValue());            
        }
        
        ValidInfo validInfo = new ValidInfo(isValid, validString);
        return new FieldValueMapResult(validInfo, valueMap);
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
        int invalidCount = 0;
        
        // pre-import hook for helper subclass
        ValidInfo preImportValidInfo = preImport();
        
        // parse / validate header row - initializes helper data structures based on actual columns
        Row headerRow = sheet.getRow(rowNumberHeader);
        ValidInfo headerValidInfo = parseHeader(headerRow);
        if (!headerValidInfo.isValid()) {
            validInput = false;
            validationMessage = headerValidInfo.getValidString();
            summaryMessage
                    = "Press 'Cancel' to terminate the import process and fix "
                    + "problems with import spreadsheet."
                    + " No action will be taken.";
            return;
        }
            
        // parse spreadsheet data rows
        for (int rowNumber = rowNumberFirstData ; rowNumber <= rowNumberLastData ; rowNumber ++) {
            Row row = sheet.getRow(rowNumber);
            if (row != null) {
                ValidInfo rowValidInfo = null;
                try {
                    rowValidInfo = parseRow(row);
                } catch (CdbException ex) {
                    validInput = false;
                    validationMessage = "Unexpected exception parsing spreadsheet row: "
                            + rowNumber + " message: "
                            + ex.getMessage();
                    summaryMessage
                            = " Press 'Cancel' to terminate the import process."
                            + " No new items will be created.";
                    return;
                }
                if (rowValidInfo != null) {
                    if (!rowValidInfo.isValid()) {
                        validInput = false;
                        invalidCount = invalidCount + 1;
                    }
                }
            }
        }
        
        int itemCount = rows.size();        
        if (itemCount == 0) {
            // nothing to import, this will disable the "next" button
            validationMessage = appendToString(
                    validationMessage, 
                    "No rows contained in spreadsheet.");
        }
        
        String modeString = "";
        if (null != getImportMode()) {
            switch (getImportMode()) {
                case UPDATE:
                    modeString = "update";
                    break;
                case CREATE:
                    modeString = "create";
                    break;
                case DELETE:
                    modeString = "delete";
                    break;
                case COMPARE:
                    modeString = "compare";
                    break;
                default:
                    break;
            }
        }

        if (validInput) {
            String summaryDetails = itemCount + " items ";
            
            String customSummaryDetails = getCustomSummaryDetails();
            if (!customSummaryDetails.isEmpty()) {
                summaryDetails = customSummaryDetails;
            }
            
            if (itemCount > 0) {
                summaryMessage
                        = "Press 'Next Step' to complete the "
                        + modeString
                        + " process. ";
                if (getImportMode() != ImportMode.COMPARE) {
                    summaryMessage = summaryMessage
                        + "This will " 
                        + modeString 
                        + " "
                        + summaryDetails
                        + " displayed in table above. This action cannot be undone.";
                }
            } else {
                summaryMessage = "Press 'Cancel' to terminate the "
                        + modeString
                        + " process. No action will be taken.";
            }
            
        } else {
            validationMessage = appendToString(
                    validationMessage, 
                    "Spreadsheet includes " + invalidCount + " invalid row(s).");
            summaryMessage = 
                    "Press 'Cancel' to terminate the "
                    + modeString
                    + " process and fix problems with spreadsheet." +
                    " No action will be taken.";
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
            if (actualColumns < 0) {
                actualColumns = 0;
            }
            if (numExpectedColumns != actualColumns) {
                isValid = false;
                validMessage
                        = "Actual number of header row columns (" + actualColumns
                        + ") does not match expected number of columns ("
                        + numExpectedColumns + ")";
            }
        }
        
        return new ValidInfo(isValid, validMessage);
    }
    
    private List<InputHandler> getHandlersForCurrentMode() {
        // return only the handlers used for the current mode
        final ImportMode mode;
        if (getImportMode() == ImportMode.COMPARE) {
            mode = ImportMode.UPDATE;
        } else {
            mode = getImportMode();
        }
        return inputHandlers.stream().filter(
                h -> h.isUsedForMode(mode)).collect(Collectors.toList());
    }
    
    private ValidInfo parseRow(Row row) throws CdbException {

        boolean isValid = true;
        String validString = "";
        
        // parse each column value into a map (cellIndex -> cellValue)        
        int colIndex;
        Map<Integer, String> cellValueMap = new HashMap<>();
        for (InputColumnModel col : inputColumnMap.values()) {
            colIndex = col.getColumnIndex();
            Cell cell = row.getCell(colIndex);
            String cellValue = parseStringCell(cell);
            cellValueMap.put(colIndex, cellValue);
            
            // check that value is present for required columns
            if ((col.isRequiredForMode(getImportMode()))
                    && ((cellValue == null) || (cellValue.isEmpty()))) {

                isValid = false;
                validString = appendToString(validString,
                        "Required value missing for " + columnNameForIndex(col.getColumnIndex()));
            }
            
            // check that value not provided in column not used in this mode
            ImportMode mode = getImportMode();
            if (getImportMode() == ImportMode.COMPARE) {
                // for compare mode, check if the column is used for updating
                mode = ImportMode.UPDATE;
            }
            if ((!col.isUsedForMode(mode))
                    && ((cellValue != null) && (!cellValue.isEmpty()))) {    
                
                if (mode != ImportMode.DELETE) {       
                    // skip this requirement for delete mode
                    isValid = false;
                    validString = appendToString(validString,
                            "Value should not be specified in current mode for "
                            + columnNameForIndex(col.getColumnIndex()));
                }
            }
        }
        
        // skip blank and comment rows
        if (isBlankRow(cellValueMap) || isCommentRow(cellValueMap)) {
            return new ValidInfo(true, "");
        }
        
        // invoke each input handler to populate row dictionary (String key -> object)
        Map<String, Object> rowDict = new HashMap<>();
        for (InputHandler handler : getHandlersForCurrentMode()) {
            ValidInfo validInfo = handler.handleInput(row, cellValueMap, rowDict);
            if (!validInfo.isValid()) {
                validString = appendToString(validString, validInfo.getValidString());
                isValid = false;
            }
        }
        
        // handle parsed row as appropriate for mode
        EntityType entity;
        CreateInfo handleCreateInfo = handleParsedRow(rowDict);
        if (!handleCreateInfo.getValidInfo().isValid()) {
            isValid = false;
            validString = appendToString(validString, handleCreateInfo.getValidInfo().getValidString());
        }

        entity = (EntityType) handleCreateInfo.getEntity();
        if (entity == null) {
            throw new CdbException("failed to create or retrieve specified item");
            
        } else {
            entity.setIsValidImport(isValid);
            entity.setValidStringImport(validString);
            rows.add(entity);
        }

        return new ValidInfo(isValid, validString);
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
    
    private CreateInfo handleParsedRow(Map<String, Object> rowDict) throws CdbException {
        
        if (getImportMode() == ImportMode.CREATE) {
            return handleParsedRowCreateMode(rowDict);
            
        } else if ((getImportMode() == ImportMode.UPDATE) || (getImportMode() == ImportMode.COMPARE)) {
            return handleParsedRowUpdateCompareMode(rowDict);
            
        } else if (getImportMode() == ImportMode.DELETE) {
            return handleParsedRowDeleteMode(rowDict);
            
        }
        
        return new CreateInfo(null, false, "Unexpected import mode.");
    }
    
    private ValidInfo invokeHandlersToUpdateEntity(EntityType entity, Map<String, Object> rowDict) {
        
        boolean isValid = true;
        String validString = "";

        // invoke each input handler for current mode to update the entity with row dictionary values
        for (InputHandler handler : getHandlersForCurrentMode()) {
            ValidInfo validInfo = handler.updateEntity(rowDict, entity);
            if (!validInfo.isValid()) {
                validString = appendToString(validString, validInfo.getValidString());
                isValid = false;
            }
        }

        return new ValidInfo(isValid, validString);
    }
    
    private CreateInfo handleParsedRowDeleteMode(Map<String, Object> rowDict) throws CdbException {
        
        boolean isValid = true;
        String validString = "";

        // retrieve existing instance and check result
        CreateInfo createInfo = null;
        EntityType entity = null;
        createInfo = retrieveEntityInstance(rowDict);
        if (createInfo == null) {
            // indicates helper doesn't support delete mode
            String msg = "Unexpected error, import helper not properly configured for delete operation.";
            throw new CdbException(msg);
        }
        
        entity = (EntityType) createInfo.getEntity();
        if (entity == null) {
            // helper must return an instance for use in the validation table,
            // even if the specified item is not located
            String msg = "Unexpected error, import helper not properly configured to retrieve items for delete operation.";
            throw new CdbException(msg);
        }
        
        ValidInfo createValidInfo = createInfo.getValidInfo();
        if (!createValidInfo.isValid()) {
            return createInfo;
        }
        
        // capture item field values for display in validation table
        FieldValueMapResult result = getFieldValueMap(entity);
        if (!result.getValidInfo().isValid()) {
            isValid = false;
            validString = appendToString(
                    validString, 
                    "Field snapshot failed: " 
                            + result.getValidInfo().getValidString()); 
        }
        FieldValueMap fieldValueMap = result.getValueMap();

        // invoke each input handler to update the entity with row dictionary values
        ValidInfo updateValidInfo = invokeHandlersToUpdateEntity(entity, rowDict);
        if (!updateValidInfo.isValid()) {
            validString = appendToString(validString, updateValidInfo.getValidString());
            isValid = false;
        }
        
        // display field values as diffs, but only if item is valid
        if (isValid) {
            String diffString = "";
            boolean first = true;
            for (String key : fieldValueMap.keySet()) {
                if (!first) {
                    diffString = diffString + "<br>";
                } else {
                    first = false;
                }
                String value = fieldValueMap.get(key);
                diffString = diffString + key + ": ";
                diffString = diffString + "<span style=\"color:red\">";
                diffString = diffString + "'" + value + "'";
                diffString = diffString + "</span>";
            }
            entity.setImportDiffs(diffString);
        }
        
        return new CreateInfo(entity, isValid, validString);
    }
    
    private CreateInfo handleParsedRowUpdateCompareMode(Map<String, Object> rowDict) throws CdbException {
        
        boolean isValid = true;
        String validString = "";
    
        // retrieve existing instance and check result
        CreateInfo createInfo = null;
        EntityType entity = null;
        createInfo = retrieveEntityInstance(rowDict);
        if (createInfo == null) {
            // indicates helper doesn't support update mode
            String msg = "Unexpected error, import helper not properly configured for update operation.";
            throw new CdbException(msg);
        }
        
        entity = (EntityType) createInfo.getEntity();
        if (entity == null) {
            // helper must return an instance for use in the validation table,
            // even if the specified item is not located
            String msg = "Unexpected error, import helper not properly configured to retrieve items for update operation.";
            throw new CdbException(msg);
        }
        
        ValidInfo createValidInfo = createInfo.getValidInfo();
        if (!createValidInfo.isValid()) {
            return createInfo;
        }
        
        // capture item field values for comparison later with updated field values
        FieldValueMapResult preUpdateValueMapResult = getFieldValueMap(entity);
        if (!preUpdateValueMapResult.getValidInfo().isValid()) {
            isValid = false;
            validString = appendToString(
                    validString, 
                    "Pre update field snapshot failed: " 
                            + preUpdateValueMapResult.getValidInfo().getValidString()); 
        }
        FieldValueMap preUpdateMap = preUpdateValueMapResult.getValueMap();
        
        // allow subclass to manipulate the retrieved entity
        ValidInfo updateEntityValidInfo = null;
        updateEntityValidInfo = updateEntityInstance(entity, rowDict);
        if (!updateEntityValidInfo.isValid()) {
            validString = appendToString(validString, updateEntityValidInfo.getValidString());
            isValid = false;
        }

        // invoke each input handler to update the entity with row dictionary values
        ValidInfo updateValidInfo = invokeHandlersToUpdateEntity(entity, rowDict);
        if (!updateValidInfo.isValid()) {
            validString = appendToString(validString, updateValidInfo.getValidString());
            isValid = false;
        }
        
        if (isValid) {

            // capture item field values for comparison later with original field values
            FieldValueMapResult postUpdateValueMapResult = getFieldValueMap(entity);
            if (!postUpdateValueMapResult.getValidInfo().isValid()) {
                isValid = false;
                validString = appendToString(
                        validString,
                        "Post update field snapshot failed: "
                        + postUpdateValueMapResult.getValidInfo().getValidString());
            }
            FieldValueMap postUpdateMap = postUpdateValueMapResult.getValueMap();

            // capture differences between original and updated values
            FieldValueDifferenceMap fieldDiffMap = FieldValueMap.difference(preUpdateMap, postUpdateMap);
            
            // flag entities with changes so we can ignore the ones that don't
            if (fieldDiffMap.keySet().isEmpty()) {
                entity.hasImportUpdates(false);
            } else {
                entity.hasImportUpdates(true);
            }

            // for compare mode, set unchanged values on entity
            if (getImportMode() == ImportMode.COMPARE) {
                String unchangedString = "";
                boolean first = true;
                for (String key : preUpdateMap.keySet()) {
                    if (!fieldDiffMap.keySet().contains(key)) {
                        if (!first) {
                            unchangedString = unchangedString + "<br>";
                        } else {
                            first = false;
                        }
                        String value = preUpdateMap.get(key);
                        unchangedString = unchangedString + key + ": ";
                        unchangedString = unchangedString + "<span style=\"color:green\">";
                        unchangedString = unchangedString + "'" + value + "'";
                        unchangedString = unchangedString + "</span>";
                    }
                }
                entity.setImportUnchanged(unchangedString);
            }

            // set update diffs string on entity if there are diffs        
            String diffString = "";
            boolean first = true;
            for (String key : fieldDiffMap.keySet()) {
                if (!first) {
                    diffString = diffString + "<br>";
                } else {
                    first = false;
                }
                FieldValueDifference diff = fieldDiffMap.get(key);
                diffString = diffString + key + ": ";
                diffString = diffString + "<span style=\"color:red\">";
                diffString = diffString + "'" + diff.getOldValue() + "'";
                diffString = diffString + "</span>";
                diffString = diffString + " -> ";
                diffString = diffString + "<span style=\"color:green\">";
                diffString = diffString + "'" + diff.getNewValue() + "'";
                diffString = diffString + "</span>";
            }
            if (entity.hasImportUpdates()) {
                entity.setImportDiffs(diffString);
            } else {
                entity.setImportDiffs("No updates detected for item.");
            }

            // skip uniqueness checks in update mode for rows already flagged as invalid,
            // otherwise error reporting is confusing
            ValidInfo uniqueValidInfo = checkEntityUniqueness(entity);
            if (!uniqueValidInfo.isValid()) {
                isValid = false;
                validString = appendToString(validString, uniqueValidInfo.getValidString());
            }
        }
        return new CreateInfo(entity, isValid, validString);
    }
    
    private CreateInfo handleParsedRowCreateMode(Map<String, Object> rowDict) throws CdbException {
        
        boolean isValid = true;
        String validString = "";

        // create new instance and check result
        CreateInfo createInfo = null;
        EntityType newEntity = null;
        createInfo = createEntityInstance(rowDict);
        newEntity = (EntityType) createInfo.getEntity();
        if (newEntity == null) {
            // helper returns null from createInstance to indicate that it won't create an item for this row
            isValid = createInfo.getValidInfo().isValid();
            validString = appendToString(validString, createInfo.getValidInfo().getValidString());
        }
            
        ValidInfo createValidInfo = createInfo.getValidInfo();
        if (!createValidInfo.isValid()) {
            validString = appendToString(validString, createValidInfo.getValidString());
            isValid = false;
        }

        // invoke each input handler to update the entity with row dictionary values
        ValidInfo updateValidInfo = invokeHandlersToUpdateEntity(newEntity, rowDict);
        if (!updateValidInfo.isValid()) {
            validString = appendToString(validString, updateValidInfo.getValidString());
            isValid = false;
        }

        ValidInfo uniqueValidInfo = checkEntityUniqueness(newEntity);
        if (!uniqueValidInfo.isValid()) {
            isValid = false;
            validString = appendToString(validString, uniqueValidInfo.getValidString());
        }

        return new CreateInfo(newEntity, isValid, validString);
    }
    
    private ValidInfo checkEntityUniqueness(EntityType entity) {
        
        boolean isValid = true;
        String validString = "";
        
        if (rows.contains(entity)) {
            // check for duplicates in spreadsheet rows
            validString = appendToString(validString, "Duplicates another row in spreadsheet.");
            isValid = false;
            
        } else {
            // check for existing duplicates in database
            try {
                getEntityController().checkItemUniqueness(entity);
            } catch (CdbException ex) {
                if (ex.getErrorMessage().startsWith("Uniqueness check not implemented by controller")) {
                    // ignore this?
                } else {
                    validString = appendToString(validString, ex.getMessage());
                    isValid = false;
                }
            }
        }

        return new ValidInfo(isValid, validString);
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
        String modeString = "";
        try {
            if (getImportMode() == ImportMode.CREATE) {
                controller.createList(rows);
                modeString = "created";
                ValidInfo result = postCreate();
                message = appendToString(message, result.getValidString());
            } else if (getImportMode() == ImportMode.UPDATE) {
                updateList();
                modeString = "updated";
            } else if (getImportMode() == ImportMode.DELETE) {
                deleteList();
                modeString = "deleted";
            } else if (getImportMode() == ImportMode.COMPARE) {
                modeString = "compared";
            }

        } catch (CdbException | RuntimeException ex) {
            return new ImportInfo(false, "Import failed. " + ex.getClass().getName());
        }
        
        if (getImportMode() != ImportMode.COMPARE) {
            message = "Operation succeeded, " + modeString + " " + rows.size() + " instances";
        } else {
            message = "Comparison complete.";
        }
        
        return new ImportInfo(true, message);
    }
    
    /**
     * Specifies helper class format name to use in GUI.  Default returns empty
     * string, subclasses override to customize.  Note that this is currently
     * specified in call to ImportFormatInfo constructor, but should change
     * subclasses to override this method instead as we add import update support.
     */
    public static String getFormatName() {
        return "";
    }
    
    /**
     * Specifies whether helper supports creation of new instances.  Defaults
     * to true. Subclasses override to customize.
     */
    public boolean supportsModeCreate() {
        return true;
    }

    /**
     * Specifies whether helper supports updating existing instances.  Defaults
     * to false. Subclasses override to customize.
     */
    public boolean supportsModeUpdate() {
        return false;
    }

    /**
     * Specifies whether helper supports deleting existing instances.  Defaults
     * to false. Subclasses override to customize.
     */
    public boolean supportsModeDelete() {
        return false;
    }

    /**
     * Provides pre-import hook for subclasses to override, e.g., to migrate
     * metadata property fields etc.
     */
    protected ValidInfo preImport() {
        return new ValidInfo(true, "");
    }
    
    protected EntityType newInvalidUpdateInstance() {
        return null;
    }

    /**
     * Specifies whether to display components on import wizard's select file tab
     * for customizing row numbers for header and first/last data rows.
     * Subclass overrides to return true for row number customization.
     */
    public boolean renderRowNumberCustomzationOptions() {
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
    
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        throw new UnsupportedOperationException("Helper does not implement createEntityInstance().");
    }
    
    /**
     * Provides callback for helper subclass to do post processing after the
     * import commit completes.  For example, the helper may need to update some
     * attribute of the imported rows based on the identifier created during the
     * database commit. Default behavior is to do nothing, subclasses override to
     * customize.
     * @return 
     */
    protected ValidInfo postCreate() {
        return new ValidInfo(true, "");
    }
    
    /**
     * Retrieves entity instance using values in rowMap.  Default implementation
     * returns null if helper is not configured to support update mode, otherwise
     * provides generic mechanism for looking up by id.  To customize, subclasses
     * can override this method, or invoke it and customize further using the result.
     */
    protected CreateInfo retrieveEntityInstance(Map<String, Object> rowMap) {
        
        if (!supportsModeUpdate()) {
            return null;
        }
        
        boolean isValid = true;
        String validString = "";
        
        EntityType existingItem = null;
        Integer itemId = (Integer) rowMap.get(KEY_EXISTING_ITEM_ID);
        if (itemId != null) {
            existingItem = (EntityType) getEntityController().findById(itemId);
            if (existingItem == null) {
                existingItem = newInvalidUpdateInstance();
                isValid = false;
                validString = "Unable to retrieve existing item with id: " + itemId;
            }
        } else {
            existingItem = newInvalidUpdateInstance();
            isValid = false;
            validString = "Must specify existing id to update item";
        }
        
        return new CreateInfo(existingItem, isValid, validString);
    }
    
    /**
     * Allows subclass to manipulate entity during update process. Default implementation
     * does nothing.
     */
    protected ValidInfo updateEntityInstance(EntityType entity, Map<String, Object> rowMap) {
        return new ValidInfo(true, "");
    }
    
    /**
     * Updates list of items in update mode.  Allows subclasses to override with
     * custom behavior.
     */
    protected void updateList() throws CdbException, RuntimeException {
        List<EntityType> updateEntities = new ArrayList<>();
        for (EntityType entity : rows) {
            if (entity.hasImportUpdates()) {
                updateEntities.add(entity);
            }
        }
        EntityControllerType controller = this.getEntityController();
        if (!updateEntities.isEmpty()) {
            controller.updateList(updateEntities);
        }
    }
    
    /**
     * Deletes list of items in delete mode.  Allows subclasses to override with
     * custom behavior.
     */
    protected void deleteList() throws CdbException, RuntimeException {
        EntityControllerType controller = this.getEntityController();
        controller.destroyList(rows, null);
    }
    
    public String getExportFilename() {
        return getFilenameBase() + " Export";
    }
    
    public IdOrNameRefColumnSpec ownerUserColumnSpec() {
        return new IdOrNameRefColumnSpec(
                "Owner User", 
                KEY_USER, 
                "setOwnerUser", 
                "ID or name of CDB owner user. Name must be unique and prefixed with '#'.", 
                "getOwnerUser",
                ColumnModeOptions.oCREATErUPDATE(),
                UserInfoController.getInstance(), 
                UserInfo.class, 
                "");
    }
    
    public IdOrNameRefColumnSpec ownerGroupColumnSpec() {
        return new IdOrNameRefColumnSpec(
                "Owner Group", 
                KEY_GROUP, 
                "setOwnerUserGroup", 
                "ID or name of CDB owner user group. Name must be unique and prefixed with '#'.", 
                "getOwnerUserGroup",
                ColumnModeOptions.oCREATErUPDATE(), 
                UserGroupController.getInstance(), 
                UserGroup.class, 
                "");
    }
    
    public IdOrNameRefListColumnSpec projectListColumnSpec() {
        return new IdOrNameRefListColumnSpec(
                "Project", 
                "itemProjectString", 
                "setItemProjectList", 
                "Comma-separated list of IDs of CDB project(s). Name must be unique and prefixed with '#'.", 
                "getItemProjectList",
                ColumnModeOptions.rCREATErUPDATE(), 
                ItemProjectController.getInstance(), 
                List.class, 
                "");
    }
    
    public IdOrNameRefListColumnSpec technicalSystemListColumnSpec(String domainName) {
        return new IdOrNameRefListColumnSpec(
                "Technical System", 
                "itemCategoryString", 
                "setItemCategoryListImport", 
                "Numeric ID of CDB technical system. Name must be unique and prefixed with '#'.", 
                "getItemCategoryList",
                ColumnModeOptions.oCREATEoUPDATE(), 
                ItemCategoryController.getInstance(), 
                List.class, 
                domainName);
    }
    
    public IdOrNameRefListColumnSpec functionListColumnSpec(String domainName) {
        return new IdOrNameRefListColumnSpec(
                "Function", 
                "itemTypeString", 
                "setItemTypeList", 
                "Numeric ID of CDB technical system. Name must be unique and prefixed with '#'.", 
                "getItemTypeList",
                ColumnModeOptions.oCREATEoUPDATE(), 
                ItemTypeController.getInstance(), 
                List.class, 
                domainName);
    }
    
    public IdOrNameRefColumnSpec locationColumnSpec() {
        return new IdOrNameRefColumnSpec(
                "Location", 
                "importLocationItemString", 
                "setImportLocationItem", 
                "Item location.", 
                "getLocationItem",
                ColumnModeOptions.oCREATEoUPDATE(), 
                ItemDomainLocationController.getInstance(), 
                ItemDomainLocation.class, 
                "");
    }
    
    public StringColumnSpec locationDetailsColumnSpec() {
        return new StringColumnSpec(
                "Location Details", 
                "locationDetails", 
                "setLocationDetails", 
                "Location details for item.", 
                "getLocationDetails",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256);
    }
    
    public IntegerColumnSpec existingItemIdColumnSpec() {
        return new IntegerColumnSpec(
                "Existing Item ID", 
                KEY_EXISTING_ITEM_ID, 
                "setImportExistingItemId", 
                "CDB ID of existing item to update.", 
                "getId",
                ColumnModeOptions.rUPDATErDELETErCOMPARE());
    }

    public BooleanColumnSpec deleteExistingItemColumnSpec() {
        return new BooleanColumnSpec(
                "Delete Existing Item", 
                KEY_DELETE_EXISTING_ITEM, 
                "setImportDeleteExistingItem", 
                "Specify TRUE to delete existing item in delete mode.",
                null,
                ColumnModeOptions.rDELETE());
    }

    protected abstract List<ColumnSpec> getColumnSpecs();
    
    public abstract EntityControllerType getEntityController();
    
    public abstract String getFilenameBase();
    
}
