package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;

/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */

/**
 *
 * @author craig
 */
public class ImportHelperCatalogConnectors extends ImportHelperConnectorBase {
    
    @Override
    public String getFilenameBase() {
        return "Catalog Ports";
    }

    @Override
    public String getItemColumnHeader() {
        return "Catalog Item";
    }

    @Override
    protected ItemDomainCatalogController getItemControllerInstance() {
        return ItemDomainCatalogController.getInstance();
    }

    @Override
    protected String getCreateMessageTypeName() {
        return "catalog item port";
    }

}