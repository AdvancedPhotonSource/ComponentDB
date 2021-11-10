package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */

/**
 *
 * @author craig
 */
public class ImportHelperCatalogPorts extends ImportHelperConnectorBase {
    
    @Override
    public String getFilenameBase() {
        return "Catalog Ports";
    }

    @Override
    protected ItemDomainCatalogController getItemControllerInstance() {
        return ItemDomainCatalogController.getInstance();
    }

    @Override
    protected String getCreateMessageTypeName() {
        return "catalog item port";
    }

    @Override
    protected List<ColumnSpec> initColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(existingItemIdColumnSpec());
        specs.add(deleteExistingItemColumnSpec());
        
        specs.add(parentItemColumnSpec(
                "Catalog Item", 
                "ID or name of parent catalog item. Name must be unique and prefixed with '#'."));
        
        specs.add(connectorNameColumnSpec("Port Name", "Name for port."));
        
        specs.add(connectorDescriptionColumnSpec("Port description."));
        
        specs.add(connectorTypeColumnSpec());
        
        return specs;
    }

}