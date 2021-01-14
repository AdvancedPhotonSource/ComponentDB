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
public class IdOrNameRefColumnSpec extends IdRefColumnSpec {

    protected String domainNameFilter = null;

    public IdOrNameRefColumnSpec(
            String header, 
            String propertyName, 
            String entitySetterMethod, 
            boolean required, 
            String description, 
            CdbEntityController controller, 
            Class paramType, 
            String domainNameFilter) {
        
        super(header, propertyName, entitySetterMethod, required, description, controller, paramType);
        this.domainNameFilter = domainNameFilter;
    }

    public IdOrNameRefColumnSpec(
            String header, 
            String propertyName, 
            String entitySetterMethod, 
            boolean required, 
            String description, 
            CdbEntityController controller, 
            Class paramType, 
            String domainNameFilter,
            String exportGetterMethod) {
        
        super(header, propertyName, entitySetterMethod, required, description, controller, paramType, exportGetterMethod);
        this.domainNameFilter = domainNameFilter;
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
