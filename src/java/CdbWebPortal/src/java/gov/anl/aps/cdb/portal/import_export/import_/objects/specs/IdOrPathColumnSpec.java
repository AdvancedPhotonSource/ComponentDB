/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.RefInputHandler;
import java.util.List;

/**
 *
 * @author craig
 */
public class IdOrPathColumnSpec extends IdOrNameRefColumnSpec {
    
    public IdOrPathColumnSpec(
            String header, 
            String importPropertyName, 
            String importSetterMethod, 
            String description,
            String exportGetterMethod,
            List<ColumnModeOptions> options, 
            CdbEntityController controller, 
            Class paramType) {
        
        super(header, 
                importPropertyName, 
                importSetterMethod, 
                description, 
                exportGetterMethod, null, 
                options,
                controller, 
                paramType, 
                null);
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
                true);
    }
}
