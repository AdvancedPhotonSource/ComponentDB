/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;

/**
 *
 * @author craig
 */
public class CustomColumnSpec extends ColumnSpec {
    
    private InputHandler inputHandler;
    
    public CustomColumnSpec(
            String header, 
            String propertyName, 
            boolean required, 
            String description,
            InputHandler handler) {
        
        super(header, propertyName, required, description);
        this.inputHandler = handler;
    }
    
    public CustomColumnSpec(
            String header, 
            String propertyName, 
            boolean required, 
            String description,
            InputHandler handler,
            String exportGetterMethod) {
        
        super(header, propertyName, required, description);
        this.exportGetterMethod = exportGetterMethod;
        this.inputHandler = handler;
    }
    
    public InputHandler getInputHandler(int colIndex) {
        inputHandler.setFirstColumnIndex(colIndex);
        return inputHandler;
    }
    
}
