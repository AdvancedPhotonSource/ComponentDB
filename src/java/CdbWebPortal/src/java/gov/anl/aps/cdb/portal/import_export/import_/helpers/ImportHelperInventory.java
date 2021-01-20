/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IntegerColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author craig
 */
public class ImportHelperInventory 
        extends ImportHelperInventoryBase<ItemDomainInventory, ItemDomainInventoryController> {

    private static final Logger LOGGER = LogManager.getLogger(ImportHelperInventory.class.getName());
    
    @Override
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new IdOrNameRefColumnSpec("Catalog Item", KEY_CATALOG_ITEM, "setCatalogItem", true, "ID or name of catalog item for inventory unit. Name must be unique and prefixed with '#'.", ItemDomainCatalogController.getInstance(), ItemDomainCatalog.class, null));
        specs.add(new StringColumnSpec("Tag", KEY_NAME, "", true, "Name of inventory unit.", 64));
        specs.add(new IntegerColumnSpec("QR ID", "qrId", "setQrId", false, "Integer QR id of inventory unit."));
        specs.add(new StringColumnSpec("Serial Number", "serialNumber", "setSerialNumber", false, "Inventory unit serial number.", 128));
        specs.add(new StringColumnSpec("Description", "description", "setDescription", false, "Description of inventory unit.", 256));
        specs.add(statusColumnSpec(5));
        specs.add(locationColumnSpec());
        specs.add(locationDetailsColumnSpec());
        specs.add(projectListColumnSpec());
        specs.add(ownerUserColumnSpec());
        specs.add(ownerGroupColumnSpec());
        
        return specs;
    } 
   
    @Override
    public ItemDomainInventoryController getEntityController() {
        return ItemDomainInventoryController.getInstance();
    }

    @Override
    public String getFilenameBase() {
        return "Component Inventory";
    }
    
    @Override
    protected void reset_() {
        super.reset_();
    }
    
    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        
        return super.createEntityInstance(rowMap);
    }  

    protected boolean ignoreDuplicates() {
        return false;
    }
}
