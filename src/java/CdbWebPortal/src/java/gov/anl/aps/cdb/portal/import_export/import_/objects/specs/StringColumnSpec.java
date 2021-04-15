/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.StringInputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
import java.util.List;

/**
 *
 * @author craig
 */
public class StringColumnSpec extends ColumnSpec {

    private int maxLength;

    public StringColumnSpec(
            String header, 
            String importPropertyName, 
            String importSetterMethod, 
            String description, 
            String exportGetterMethod,
            List<ColumnModeOptions> options,
            int maxLength) {

        super(
                header, 
                importPropertyName, 
                importSetterMethod, 
                description, 
                exportGetterMethod, 
                options);
        this.maxLength = maxLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    @Override
    public InputHandler getInputHandler(int colIndex) {
        return new StringInputHandler(
                colIndex,
                getHeader(),
                getPropertyName(),
                getEntitySetterMethod(),
                getMaxLength());
    }
}
