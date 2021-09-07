/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.import_export.export.objects.handlers.BlankColumnOutputHandler;
import gov.anl.aps.cdb.portal.import_export.export.objects.handlers.OutputHandler;
import gov.anl.aps.cdb.portal.import_export.export.objects.handlers.SimpleOutputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnSpecInitInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ExportMode;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ImportMode;
import gov.anl.aps.cdb.portal.import_export.import_.objects.InputColumnModel;
import gov.anl.aps.cdb.portal.import_export.import_.objects.OutputColumnModel;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public abstract class ColumnSpec {
    
    public final static String BLANK_COLUMN_EXPORT_METHOD = "blankExportGetterMethod";

    private String header;
    private String propertyName;
    private String entitySetterMethod;
    private String description;
    protected String exportGetterMethod;
    protected String exportTransferGetterMethod;
    
    private Map<ImportMode, ColumnModeOptions> columnModeOptionsMap = new HashMap<>();
        
    public ColumnSpec() {
    }
    
    public ColumnSpec(
            String description, 
            List<ColumnModeOptions> options) {
        
        this.description = description;
        if (options != null) {
            for (ColumnModeOptions option : options) {
                this.addColumnModeOptions(option);
            }
        }
    }

    public ColumnSpec(
            String header, 
            String importPropertyName, 
            String description, 
            List<ColumnModeOptions> options) {

        this(description, options);
        this.header = header;
        this.propertyName = importPropertyName;
    }

    public ColumnSpec(
            String header, 
            String importPropertyName, 
            String importSetterMethod, 
            String description, 
            List<ColumnModeOptions> options) {

        this(header, importPropertyName, description, options);
        this.entitySetterMethod = importSetterMethod;
    }

    /**
     * Creates a column spec appropriate for import and export.
     */
    public ColumnSpec(
            String header, 
            String importPropertyName, 
            String importSetterMethod, 
            String description, 
            String exportGetterMethod,
            List<ColumnModeOptions> options) {
        
        this(header, importPropertyName, importSetterMethod, description, options);
        this.exportGetterMethod = exportGetterMethod;
    }

    /**
     * Creates a column spec appropriate for import and export.
     */
    public ColumnSpec(
            String header, 
            String importPropertyName, 
            String importSetterMethod, 
            String description, 
            String exportGetterMethod,
            String exportTransferGetterMethod,
            List<ColumnModeOptions> options) {
        
        this(header, importPropertyName, importSetterMethod, description, exportGetterMethod, options);
        this.exportTransferGetterMethod = exportTransferGetterMethod;
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

    public String getDescription() {
        return description;
    }
    
    public String getExportGetterMethod() {
        return exportGetterMethod;
    }
    
    public String getExportTransferGetterMethod() {
        return exportTransferGetterMethod;
    }
    
    public int getInputTemplateColumns(
            int colIndex,
            List<InputColumnModel> inputColumns_io) {

        inputColumns_io.add(createInputColumnModel(colIndex));
        return 1;
    }
    
    public ColumnSpecInitInfo initialize(
            int colIndex,
            Map<Integer, String> headerValueMap) {
        
        ColumnSpecInitInfo initInfo = initialize_(colIndex, headerValueMap);
        
        if (initInfo.getValidInfo().isValid()) {        
            for (InputHandler handler : initInfo.getInputHandlers()) {
                handler.setColumnSpec(this);
            }

            for (InputColumnModel inputColumn : initInfo.getInputColumns()) {
                inputColumn.setColumnSpec(this);
            }
        }

        return initInfo;
    }
    
    /**
     * Initializes lists of input columns, input handlers, and output columns.
     * Subclass can override default implementation to customize.
     */
    protected ColumnSpecInitInfo initialize_(
            int colIndex,
            Map<Integer, String> headerValueMap) {
        
        boolean isValid = true;
        String validString = "";
        
        List<InputColumnModel> inputColumns = new ArrayList<>();
        List<InputHandler> inputHandlers = new ArrayList<>();
        List<OutputColumnModel> outputColumns = new ArrayList<>();

        String headerValue = headerValueMap.get(colIndex);
        if (headerValue == null) {
            headerValue = "";
        }
        if ((headerValue.isEmpty()) || (!headerValue.equals(getHeader()))) {
            isValid = false;
            validString = "Import spreadsheet is missing expected column: '" 
                    + getHeader() + "', actual column encountered: '" + headerValue + "'.";
        } else {
            inputColumns.add(createInputColumnModel(colIndex));
            inputHandlers.add(getInputHandler(colIndex));
            outputColumns.add(createOutputColumnModel(colIndex));
        }

        ValidInfo validInfo = new ValidInfo(isValid, validString);
        return new ColumnSpecInitInfo(validInfo, 1, inputColumns, inputHandlers, outputColumns);
    }
    
    private InputColumnModel createInputColumnModel(int colIndex) {
        return new InputColumnModel(
                colIndex,
                getHeader(),
                getDescription());
    }
    
    private OutputColumnModel createOutputColumnModel(int colIndex) {
        return new OutputColumnModel(
                getHeader(),
                getPropertyName());
    }
    
    protected abstract InputHandler getInputHandler(int colIndex);
    
    public OutputHandler getOutputHandler(ExportMode exportMode) {
        
        // determine getter method base on mode
        // NOTE: to specify that a column should be blank in transfer mode,
        // but contain a value in export mode, the constant BLANK_COLUMN_EXPORT_METHOD
        // should be specified as the exportTransferGetterMethod, and a regular getter
        // method specified for exportGetterMode.
        String methodName = null;
        if (exportMode == ExportMode.TRANSFER) {
            // default to regular export getter method if transfer method not specified
            methodName = exportTransferGetterMethod;
            if (methodName == null) {
                methodName = exportGetterMethod;
            }
        } else {
            methodName = exportGetterMethod;
        }
        
        if (methodName == null || methodName.isBlank() || methodName == BLANK_COLUMN_EXPORT_METHOD) {
            return new BlankColumnOutputHandler(getHeader(), getDescription());
        } else {
            return createOutputHandler(methodName);
        }
    }
    
    protected OutputHandler createOutputHandler(String getterMethod) {
        return new SimpleOutputHandler(getHeader(), getDescription(), getterMethod);
    }
    
    private void addColumnModeOptions(ColumnModeOptions options) {
        columnModeOptionsMap.put(options.getMode(), options);
    }

    public boolean isUsedForMode(ImportMode mode) {
        return columnModeOptionsMap.containsKey(mode);
    }
    
    public boolean isRequiredForMode(ImportMode mode) {
        if (!columnModeOptionsMap.containsKey(mode)) {
            return false;
        } else {
            return columnModeOptionsMap.get(mode).isRequired();
        }
    }
    
    public boolean isUnchangeable() {
        ColumnModeOptions opts = columnModeOptionsMap.get(ImportMode.UPDATE);
        return (opts != null) && (opts.isUnchangeable());
    }

}
