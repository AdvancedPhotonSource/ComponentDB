/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.IntegerInputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;

/**
 *
 * @author craig
 */
public class IntegerColumnSpec extends ColumnSpec {

    public IntegerColumnSpec(
            String header, 
            String propertyName, 
            String entitySetterMethod, 
            boolean required, 
            String description) {
        
        super(header, propertyName, entitySetterMethod, required, description);
    }

    public IntegerColumnSpec(
            String header, 
            String propertyName, 
            String entitySetterMethod, 
            boolean required, 
            String description,
            String exportGetterMethod,
            boolean updateOnly) {
        
        super(header, propertyName, entitySetterMethod, required, description, exportGetterMethod, updateOnly);
    }

    @Override
    public InputHandler getInputHandler(int colIndex) {
        return new IntegerInputHandler(
                colIndex, getHeader(), getPropertyName(), getEntitySetterMethod());
    }

}
