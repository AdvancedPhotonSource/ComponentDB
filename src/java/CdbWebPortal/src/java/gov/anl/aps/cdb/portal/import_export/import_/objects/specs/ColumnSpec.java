/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.import_export.export.objects.handlers.OutputHandler;
import gov.anl.aps.cdb.portal.import_export.export.objects.handlers.SimpleOutputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnSpecInitInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.InputColumnModel;
import gov.anl.aps.cdb.portal.import_export.import_.objects.OutputColumnModel;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
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
    private boolean required;
    private String description;
    protected String exportGetterMethod;
    
    // column value can only be specified in update mode
    private boolean updateOnly = false;
    
    public ColumnSpec() {
    }
    
    public ColumnSpec(String description) {
        this.description = description;
    }

    public ColumnSpec(
            String header, String propertyName, boolean required, String description) {

        this(description);
        this.header = header;
        this.propertyName = propertyName;
        this.required = required;
    }

    public ColumnSpec(
            String header, String propertyName, String entitySetterMethod, boolean required, String description) {

        this(header, propertyName, required, description);
        this.entitySetterMethod = entitySetterMethod;
    }

    /**
     * Creates a column spec appropriate for import and export.
     */
    public ColumnSpec(
            String header, 
            String importPropertyName, 
            String importSetterMethod, 
            boolean importRequired, 
            String description, 
            String exportGetterMethod,
            boolean updateOnly) {

        this(header, importPropertyName, importSetterMethod, importRequired, description);
        this.exportGetterMethod = exportGetterMethod;
        this.updateOnly = updateOnly;
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
    
    public String getExportGetterMethod() {
        return exportGetterMethod;
    }
    
    public boolean isUpdateOnly() {
        return updateOnly;
    }

    public int getInputTemplateColumns(
            int colIndex,
            List<InputColumnModel> inputColumns_io) {

        inputColumns_io.add(getInputColumnModel(colIndex));
        return 1;
    }
    
    public ColumnSpecInitInfo initialize(
            int colIndex,
            Map<Integer, String> headerValueMap,
            List<InputColumnModel> inputColumns_io,
            List<InputHandler> inputHandlers_io,
            List<OutputColumnModel> outputColumns_io) {

        inputColumns_io.add(getInputColumnModel(colIndex));
        inputHandlers_io.add(getInputHandler(colIndex));
        outputColumns_io.add(getOutputColumnModel(colIndex));

        ValidInfo validInfo = new ValidInfo(true, "");
        return new ColumnSpecInitInfo(validInfo, 1);
}
    
    public InputColumnModel getInputColumnModel(int colIndex) {
        return new InputColumnModel(
                colIndex,
                getHeader(),
                isRequired(),
                getDescription(),
                isUpdateOnly());
    }
    
    public OutputColumnModel getOutputColumnModel(int colIndex) {
        return new OutputColumnModel(
                getHeader(),
                getPropertyName());
    }

    public abstract InputHandler getInputHandler(int colIndex);
    
    public OutputHandler getOutputHandler() {
        if (exportGetterMethod == null || exportGetterMethod.isBlank()) {
            return null;
        }
        return new SimpleOutputHandler(getHeader(), getDescription(), getExportGetterMethod());
    }

}
