/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
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
    protected List<ColumnSpec> initColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(existingItemIdColumnSpec());
        specs.add(deleteExistingItemColumnSpec());

        specs.add(new IdOrNameRefColumnSpec(
                "Catalog Item", 
                KEY_CATALOG_ITEM, 
                "setCatalogItem", 
                "ID or name of catalog item for inventory unit. Name must be unique and prefixed with '#'.", 
                "getCatalogItem", 
                null,
                ColumnModeOptions.rCREATErUPDATE(), 
                ItemDomainCatalogController.getInstance(), 
                ItemDomainCatalog.class, 
                null));
        
        specs.add(new StringColumnSpec(
                "Tag", 
                KEY_NAME, 
                "", 
                "Name of inventory unit.", 
                "getName",
                ColumnModeOptions.rCREATErUPDATE(), 
                64));
        
        specs.add(new IntegerColumnSpec(
                "QR ID", 
                "qrId", 
                "setQrId", 
                "Integer QR id of inventory unit.", 
                "getQrId", 
                null,
                ColumnModeOptions.oCREATEoUPDATE()));
        
        specs.add(new StringColumnSpec(
                "Serial Number", 
                "serialNumber", 
                "setSerialNumber", 
                "Inventory unit serial number.", 
                "getSerialNumber",
                ColumnModeOptions.oCREATEoUPDATE(), 
                128));
        
        specs.add(new StringColumnSpec(
                "Description", 
                "description", 
                "setDescription", 
                "Description of inventory unit.", 
                "getDescription",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
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

}
