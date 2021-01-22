/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.import_export.export.objects.handlers.OutputHandler;
import gov.anl.aps.cdb.portal.import_export.export.objects.handlers.RefOutputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.RefInputHandler;

/**
 *
 * @author craig
 */
public class IdRefColumnSpec extends ColumnSpec {

    protected CdbEntityController controller;
    protected Class paramType;

    public IdRefColumnSpec(
            String header, 
            String propertyName, 
            String entitySetterMethod, 
            boolean requiredForCreate, 
            String description, 
            CdbEntityController controller, 
            Class paramType) {
        
        super(header, propertyName, entitySetterMethod, requiredForCreate, description);
        this.controller = controller;
        this.paramType = paramType;
    }

    public IdRefColumnSpec(
            String header, 
            String propertyName, 
            String entitySetterMethod, 
            boolean requiredForCreate, 
            String description, 
            CdbEntityController controller, 
            Class paramType,
            String exportGetterMethod,
            boolean updateOnly,
            boolean requiredForUpdate) {
        
        super(
                header, 
                propertyName, 
                entitySetterMethod, 
                requiredForCreate, 
                description, 
                exportGetterMethod, 
                updateOnly,
                requiredForUpdate);
        
        this.controller = controller;
        this.paramType = paramType;
    }

    @Override
    public InputHandler getInputHandler(int colIndex) {
        return new RefInputHandler(
                colIndex,
                getHeader(),
                getPropertyName(),
                getEntitySetterMethod(),
                controller,
                paramType,
                true,
                true);
    }

    @Override
    public OutputHandler getOutputHandler() {
        return new RefOutputHandler(
                getHeader(),
                getDescription(),
                getExportGetterMethod(),
                true);
    }
}
