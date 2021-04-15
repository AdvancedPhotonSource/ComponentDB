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

    private String header;
    private String propertyName;
    private String entitySetterMethod;
    private String description;
    protected String exportGetterMethod;
    
    private Map<ImportMode, ColumnModeOptions> columnModeOptionsMap = new HashMap<>();
        
    public ColumnSpec() {
    }
    
    public ColumnSpec(
            String description, 
            List<ColumnModeOptions> options) {
        
        this.description = description;
        for (ColumnModeOptions option : options) {
            this.addColumnModeOptions(option);
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
    
    public int getInputTemplateColumns(
            int colIndex,
            List<InputColumnModel> inputColumns_io) {

        inputColumns_io.add(getInputColumnModel(colIndex));
        return 1;
    }
    
    public ColumnSpecInitInfo initialize(
            int colIndex,
            Map<Integer, String> headerValueMap) {
        
        ColumnSpecInitInfo initInfo = initialize_(colIndex, headerValueMap);
        
        for (InputHandler handler : initInfo.getInputHandlers()) {
            handler.setColumnSpec(this);
        }
        
        for (InputColumnModel inputColumn : initInfo.getInputColumns()) {
            inputColumn.setColumnSpec(this);
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
        
        List<InputColumnModel> inputColumns = new ArrayList<>();
        List<InputHandler> inputHandlers = new ArrayList<>();
        List<OutputColumnModel> outputColumns = new ArrayList<>();

        inputColumns.add(getInputColumnModel(colIndex));
        inputHandlers.add(getInputHandler(colIndex));
        outputColumns.add(getOutputColumnModel(colIndex));

        ValidInfo validInfo = new ValidInfo(true, "");
        return new ColumnSpecInitInfo(validInfo, 1, inputColumns, inputHandlers, outputColumns);
    }
    
    private InputColumnModel getInputColumnModel(int colIndex) {
        return new InputColumnModel(
                colIndex,
                getHeader(),
                getDescription());
    }
    
    private OutputColumnModel getOutputColumnModel(int colIndex) {
        return new OutputColumnModel(
                getHeader(),
                getPropertyName());
    }

    protected abstract InputHandler getInputHandler(int colIndex);
    
    public OutputHandler getOutputHandler() {
        if (exportGetterMethod == null || exportGetterMethod.isBlank()) {
            return new BlankColumnOutputHandler(getHeader(), getDescription());
        }
        return new SimpleOutputHandler(getHeader(), getDescription(), getExportGetterMethod());
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

}
