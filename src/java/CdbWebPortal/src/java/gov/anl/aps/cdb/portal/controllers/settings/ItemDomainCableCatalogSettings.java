/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;

/**
 *
 * @author djarosz
 */
public class ItemDomainCableCatalogSettings extends ItemSettings<ItemDomainCableCatalogController> {
    
    public ItemDomainCableCatalogSettings(ItemDomainCableCatalogController parentController) {
        super(parentController);
    }
    
    @Override
    public PropertyType getCoreMetadataPropertyType() {
        PropertyType propertyType = PropertyTypeFacade.getInstance().findByName(ItemDomainCableCatalog.CABLE_CATALOG_INTERNAL_PROPERTY_TYPE);
        return propertyType;
    }
     
}
