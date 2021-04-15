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
        return new IntegerInputHandler(
                colIndex, getHeader(), getPropertyName(), getEntitySetterMethod());
    }

}
