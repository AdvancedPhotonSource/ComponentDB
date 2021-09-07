/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.import_export.export.objects.handlers.BlankColumnOutputHandler;
import gov.anl.aps.cdb.portal.import_export.export.objects.handlers.HierarchyOutputHandler;
import gov.anl.aps.cdb.portal.import_export.export.objects.handlers.OutputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnSpecInitInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ExportMode;
import gov.anl.aps.cdb.portal.import_export.import_.objects.InputColumnModel;
import gov.anl.aps.cdb.portal.import_export.import_.objects.OutputColumnModel;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.HierarchyHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class NameHierarchyColumnSpec extends ColumnSpec {
    
    private String colNamePattern;
    private String keyName;
    private String keyIndent;
    private HierarchyHandler inputHandler;
    private int numTemplateColumns;
    
    public NameHierarchyColumnSpec(
            String description,
            List<ColumnModeOptions> options,
            String colNamePattern,
            String keyName,
            String keyIndent,
            int numTemplateColumns) {
        
        super(description, options);
        
        this.colNamePattern = colNamePattern;
        this.keyName = keyName;
        this.keyIndent = keyIndent;
        this.numTemplateColumns = numTemplateColumns;
    }
    
    @Override
    public int getInputTemplateColumns(
            int colIndex,
            List<InputColumnModel> inputColumns_io) {

        int colNum = 1;
        for (int i = colIndex ; i < colIndex + numTemplateColumns ; i++) {
            String columnHeader = colNamePattern + " " + String.valueOf(colNum);
            InputColumnModel inputColumnModel = 
                    new InputColumnModel(i, columnHeader, getDescription());
            inputColumns_io.add(inputColumnModel);
            colNum = colNum + 1;
        }
        return numTemplateColumns;
    }
    
    @Override
    public ColumnSpecInitInfo initialize_(
            int colIndex,
            Map<Integer, String> headerValueMap) {

        boolean isValid = true;
        String validString = "";
        
        boolean done = false;
        boolean foundLevel = false;
        int firstLevelIndex = -1;
        int lastLevelIndex = -1;
        int currIndex = colIndex;
        
        List<InputColumnModel> inputColumns = new ArrayList<>();
        List<InputHandler> inputHandlers = new ArrayList<>();
        List<OutputColumnModel> outputColumns = new ArrayList<>();
        
        while (!done) {
            String columnHeader = headerValueMap.get(currIndex);
            // check to see if this is a "level" column
            if (columnHeader.startsWith(colNamePattern)) {
                InputColumnModel inputColumnModel = 
                        new InputColumnModel(currIndex, columnHeader, getDescription());
                inputColumns.add(inputColumnModel);
                foundLevel = true;
                if (firstLevelIndex == -1) {
                    firstLevelIndex = currIndex;
                }
                lastLevelIndex = currIndex;
                currIndex = currIndex + 1;
            } else {
                done = true;
            }                        
        }
        
        if (!foundLevel) {
            // didn't find any "Level" columns, so fail
            isValid = false;
            String msg = "One or more 'Level' columns is required.";
            validString = msg;
            
        } else {
            // add single handler for multiple "level" columns
            inputHandler = new HierarchyHandler(
                    firstLevelIndex, lastLevelIndex, 128, keyName, keyIndent);
            inputHandlers.add(inputHandler);
            outputColumns.add(new OutputColumnModel("Parent Path", "importPath"));
            outputColumns.add(new OutputColumnModel("Name", "name"));
        }

        ValidInfo validInfo = new ValidInfo(isValid, validString);
        int numColumns = lastLevelIndex - firstLevelIndex + 1;
        return new ColumnSpecInitInfo(validInfo, numColumns, inputColumns, inputHandlers, outputColumns);
}
    
    @Override
    public InputHandler getInputHandler(int colIndex) {
        return inputHandler;
    }

    public OutputHandler getOutputHandler(ExportMode exportMode) {
        if (exportMode == ExportMode.EXPORT) {
            return new BlankColumnOutputHandler(getHeader(), getDescription());
        } else {
            return new HierarchyOutputHandler();
        }
    }
}
