/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;

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
    protected String getItemColumnHeader() {
        return "Cable Catalog Item";
    }
    
    @Override
    protected ItemDomainCableCatalogController getItemControllerInstance() {
        return ItemDomainCableCatalogController.getInstance();
    }
}
