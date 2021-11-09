/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author craig
 */
public class ImportHelperCableCatalogConnectors extends ImportHelperConnectorBase {
    
    @Override
    public String getFilenameBase() {
        return "Cable Catalog Connectors";
    }

    @Override
    protected ItemDomainCableCatalogController getItemControllerInstance() {
        return ItemDomainCableCatalogController.getInstance();
    }
    
    @Override
    protected String getCreateMessageTypeName() {
        return "cable catalog item connector";
    }

    @Override
    protected List<ColumnSpec> initColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(parentItemColumnSpec(
                "Cable Catalog Item", 
                "ID or name of parent cable catalog item. Name must be unique and prefixed with '#'."));
        
        specs.add(connectorNameColumnSpec("Connector Name", "Name for cable connector."));
        
        specs.add(cableEndColumnSpec());
        
        specs.add(connectorDescriptionColumnSpec("Connector description."));
        
        specs.add(connectorTypeColumnSpec());
        
        return specs;
    }

}
