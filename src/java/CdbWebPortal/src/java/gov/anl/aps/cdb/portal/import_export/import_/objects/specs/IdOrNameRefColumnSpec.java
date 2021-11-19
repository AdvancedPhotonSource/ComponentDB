/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.RefInputHandler;
import java.util.List;

/**
 *
 * @author craig
 */
public class IdOrNameRefColumnSpec extends IdRefColumnSpec {

    protected String domainNameFilter = null;

    public IdOrNameRefColumnSpec(
            String header, 
            String importPropertyName, 
            String importSetterMethod, 
            String description, 
            String exportGetterMethod, 
            String exportTransferGetterMethod, 
            List<ColumnModeOptions> options, 
            CdbEntityController controller, 
            Class paramType, 
            String domainNameFilter) {
        
        super(
                header, 
                importPropertyName, 
                importSetterMethod, 
                description, 
                exportGetterMethod,
                exportTransferGetterMethod,
                options,
                controller, 
                paramType);
        
        this.domainNameFilter = domainNameFilter;
    }

    public IdOrNameRefColumnSpec(
            String header, 
            String importPropertyName, 
            String importSetterMethod, 
            String description, 
            String exportGetterMethod, 
            String exportTransferGetterMethod, 
            List<ColumnModeOptions> options, 
            List<CdbEntityController> controllers, 
            Class paramType, 
            String domainNameFilter) {
        
        super(
                header, 
                importPropertyName, 
                importSetterMethod, 
                description, 
                exportGetterMethod,
                exportTransferGetterMethod,
                options,
                controllers, 
                paramType);
        
        this.domainNameFilter = domainNameFilter;
    }

    @Override
    public InputHandler getInputHandler(int colIndex) {
        return new RefInputHandler(
                colIndex,
                getHeader(),
                getPropertyName(),
                getEntitySetterMethod(),
                controllers,
                paramType,
                domainNameFilter,
                false,
                true,
                false);
    }

}
