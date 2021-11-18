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
public class MultiDomainRefColumnSpec extends IdOrNameRefColumnSpec {
    
    private boolean idOnly;
    private boolean singleValue;
    private boolean allowPaths;
    
    public MultiDomainRefColumnSpec(
            String header,
            String importPropertyName,
            String importSetterMethod,
            String description,
            String exportGetterMethod,
            String exportTransferGetterMethod,
            List<ColumnModeOptions> options,
            List<CdbEntityController> controllers,
            Class paramType,
            String domainNameFilter,
            boolean idOnly,
            boolean singleValue,
            boolean allowPaths) {

        super(header,
                importPropertyName,
                importSetterMethod,
                description,
                exportGetterMethod, 
                exportTransferGetterMethod,
                options,
                controllers,
                paramType,
                domainNameFilter);
        
        this.idOnly = idOnly;
        this.singleValue = singleValue;
        this.allowPaths = allowPaths;
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
                idOnly,
                singleValue,
                allowPaths);
    }
}
