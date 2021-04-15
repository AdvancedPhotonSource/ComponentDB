/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
import java.util.List;

/**
 *
 * @author craig
 */
public class CustomColumnSpec extends ColumnSpec {
    
    private InputHandler inputHandler;
    
    public CustomColumnSpec(
            String header, 
            String importPropertyName, 
            String description,
            String exportGetterMethod, 
            List<ColumnModeOptions> options,
            InputHandler handler) {
        
        super(header, importPropertyName, null, description, exportGetterMethod, options);
        this.inputHandler = handler;
    }
    
    public InputHandler getInputHandler(int colIndex) {
        inputHandler.setFirstColumnIndex(colIndex);
        return inputHandler;
    }
    
}
