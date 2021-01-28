/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.IntegerInputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
import java.util.List;

/**
 *
 * @author craig
 */
public class IntegerColumnSpec extends ColumnSpec {

    public IntegerColumnSpec(
            String header, 
            String propertyName, 
            String entitySetterMethod, 
            boolean requiredForCreate, 
            String description) {
        
        super(header, propertyName, entitySetterMethod, requiredForCreate, description);
    }

    public IntegerColumnSpec(
            String header, 
            String propertyName, 
            String entitySetterMethod, 
            boolean requiredForCreate, 
            String description,
            String exportGetterMethod,
            boolean updateOnly) {
        
        super(
                header, 
                propertyName, 
                entitySetterMethod, 
                requiredForCreate, 
                description, 
                exportGetterMethod, 
                updateOnly,
                requiredForCreate);
    }

    public IntegerColumnSpec(
            String header, 
            String propertyName, 
            String entitySetterMethod, 
            String description,
            String exportGetterMethod,
            List<ColumnModeOptions> options) {
        
        super(
                header, 
                propertyName, 
                entitySetterMethod, 
                description, 
                exportGetterMethod,
                options);
    }

    @Override
    public InputHandler getInputHandler(int colIndex) {
        return new IntegerInputHandler(
                colIndex, getHeader(), getPropertyName(), getEntitySetterMethod());
    }

}
