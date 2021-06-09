/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.FloatInputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
import java.util.List;

/**
 *
 * @author craig
 */
public class FloatColumnSpec extends ColumnSpec {

    public FloatColumnSpec(
            String header, 
            String importPropertyName, 
            String importSetterMethod, 
            String description,
            String exportGetterMethod,
            String exportTransferGetterMethod,
            List<ColumnModeOptions> options) {
        
        super(
                header, 
                importPropertyName, 
                importSetterMethod, 
                description, 
                exportGetterMethod,
                exportTransferGetterMethod,
                options);
    }

    @Override
    public InputHandler getInputHandler(int colIndex) {
        return new FloatInputHandler(
                colIndex, getHeader(), getPropertyName(), getEntitySetterMethod());
    }
}

