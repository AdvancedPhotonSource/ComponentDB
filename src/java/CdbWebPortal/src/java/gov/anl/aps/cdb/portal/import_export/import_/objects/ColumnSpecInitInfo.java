/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author craig
 */
public class ColumnSpecInitInfo {

    private ValidInfo validInfo;
    private int numColumns = 0;
    List<InputColumnModel> inputColumns;
    List<InputHandler> inputHandlers;
    List<OutputColumnModel> outputColumns;  
    
    public ColumnSpecInitInfo(
            ValidInfo validInfo, 
            int numColumns) {
        
        this.validInfo = validInfo;
        this.numColumns = numColumns;
    }
    
    public ColumnSpecInitInfo(
            ValidInfo validInfo, 
            int numColumns, 
            List<InputColumnModel> inputColumns, 
            List<InputHandler> inputHandlers, 
            List<OutputColumnModel> outputColumns) {
        
        this(validInfo, numColumns);
        this.inputColumns = inputColumns;
        this.inputHandlers = inputHandlers;
        this.outputColumns = outputColumns;
    }
    
    public ValidInfo getValidInfo() {
        return validInfo;
    }

    public int getNumColumns() {
        return numColumns;
    }    
    
    public List<InputColumnModel> getInputColumns() {
        if (inputColumns == null) {
            inputColumns = new ArrayList<>();
        }
        return inputColumns;
    }
    
    public List<InputHandler> getInputHandlers() {
        if (inputHandlers == null) {
            inputHandlers = new ArrayList<>();
        }
        return inputHandlers;
    }
    
    public List<OutputColumnModel> getOutputColumns() {
        if (outputColumns == null) {
            outputColumns = new ArrayList<>();
        }
        return outputColumns;
    }
}
