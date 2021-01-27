/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnSpecInitInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ImportMode;
import gov.anl.aps.cdb.portal.import_export.import_.objects.InputColumnModel;
import gov.anl.aps.cdb.portal.import_export.import_.objects.OutputColumnModel;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.HierarchyHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
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
            String colNamePattern,
            String keyName,
            String keyIndent,
            String description,
            int numTemplateColumns) {
        
        super(description);
        
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
            inputColumnModel.addColumnModeOptions(new ColumnModeOptions(ImportMode.CREATE, false));
            inputColumns_io.add(inputColumnModel);
            colNum = colNum + 1;
        }
        return numTemplateColumns;
    }
    
    @Override
    public ColumnSpecInitInfo initialize(
            int colIndex,
            Map<Integer, String> headerValueMap,
            List<InputColumnModel> inputColumns_io,
            List<InputHandler> inputHandlers_io,
            List<OutputColumnModel> outputColumns_io) {

        boolean isValid = true;
        String validString = "";
        
        boolean done = false;
        boolean foundLevel = false;
        int firstLevelIndex = -1;
        int lastLevelIndex = -1;
        int currIndex = colIndex;
        
        while (!done) {
            String columnHeader = headerValueMap.get(currIndex);
            // check to see if this is a "level" column
            if (columnHeader.startsWith(colNamePattern)) {
                InputColumnModel inputColumnModel = 
                        new InputColumnModel(currIndex, columnHeader, getDescription());
                inputColumnModel.addColumnModeOptions(new ColumnModeOptions(ImportMode.CREATE, false));
                inputColumns_io.add(inputColumnModel);
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
            String msg = "one or more 'Level' columns is required";
            validString = msg;
            
        } else {
            // add single handler for multiple "level" columns
            inputHandler = new HierarchyHandler(
                    firstLevelIndex, lastLevelIndex, 128, keyName, keyIndent);
            inputHandlers_io.add(inputHandler);
            outputColumns_io.add(new OutputColumnModel("Parent Path", "importPath"));
            outputColumns_io.add(new OutputColumnModel("Name", "name"));
        }

        ValidInfo validInfo = new ValidInfo(isValid, validString);
        int numColumns = lastLevelIndex - firstLevelIndex + 1;
        return new ColumnSpecInitInfo(validInfo, numColumns);
}
    
    @Override
    public InputHandler getInputHandler(int colIndex) {
        return inputHandler;
    }
}
