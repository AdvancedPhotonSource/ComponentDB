/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.StringInputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;

/**
 *
 * @author craig
 */
public class StringColumnSpec extends ColumnSpec {

    private int maxLength;

    public StringColumnSpec(
            String header, 
            String propertyName, 
            String entitySetterMethod, 
            boolean requiredForCreate, 
            String description, 
            int maxLength) {

        super(header, propertyName, entitySetterMethod, requiredForCreate, description);
        this.maxLength = maxLength;
    }

    public StringColumnSpec(
            String header, 
            String propertyName, 
            String entitySetterMethod, 
            boolean requiredForCreate, 
            String description,
            int maxLength, 
            String exportGetterMethod) {

        super(
                header, 
                propertyName, 
                entitySetterMethod, 
                requiredForCreate, 
                description, 
                exportGetterMethod, 
                false, 
                requiredForCreate);
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
