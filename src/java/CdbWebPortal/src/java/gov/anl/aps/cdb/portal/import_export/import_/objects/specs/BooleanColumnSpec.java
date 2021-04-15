/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.BooleanInputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
import java.util.List;

/**
 *
 * @author craig
 */
public class BooleanColumnSpec extends ColumnSpec {

    public BooleanColumnSpec(
            String header, 
            String importPropertyName, 
            String importSetterMethod, 
            String description,
            String exportGetterMethod,
            List<ColumnModeOptions> options) {
        
        super(
                header, 
                importPropertyName, 
                importSetterMethod, 
                description, 
                exportGetterMethod,
                options);
    }

    @Override
    public InputHandler getInputHandler(int colIndex) {
        return new BooleanInputHandler(
                colIndex, getHeader(), getPropertyName(), getEntitySetterMethod());
    }
}
