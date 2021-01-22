/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.import_export.export.objects.handlers.OutputHandler;
import gov.anl.aps.cdb.portal.import_export.export.objects.handlers.RefOutputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.RefInputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;

/**
 *
 * @author craig
 */
public class IdOrNameRefListColumnSpec extends IdOrNameRefColumnSpec {

    public IdOrNameRefListColumnSpec(
            String header, 
            String propertyName, 
            String entitySetterMethod, 
            boolean requiredForCreate, 
            String description, 
            CdbEntityController controller, 
            Class paramType, 
            String domainNameFilter) {
        
        super(
                header, 
                propertyName, 
                entitySetterMethod, 
                requiredForCreate, 
                description, 
                controller, 
                paramType, 
                domainNameFilter);
    }

    public IdOrNameRefListColumnSpec(
            String header, 
            String propertyName, 
            String entitySetterMethod, 
            boolean requiredForCreate, 
            String description, 
            CdbEntityController controller, 
            Class paramType, 
            String domainNameFilter,
            String exportGetterMethod,
            boolean updateOnly,
            boolean requiredForUpdate) {
        
        super(
                header, 
                propertyName, 
                entitySetterMethod, 
                requiredForCreate, 
                description, 
                controller, 
                paramType, 
                domainNameFilter, 
                exportGetterMethod,
                updateOnly,
                requiredForUpdate);
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
                domainNameFilter,
                false,
                false);
    }

    @Override
    public OutputHandler getOutputHandler() {
        return new RefOutputHandler(
                getHeader(),
                getDescription(),
                getExportGetterMethod(),
                false);
    }
}
