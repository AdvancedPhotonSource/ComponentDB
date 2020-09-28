/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableInventoryController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IntegerColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author craig
 */
public class ImportHelperCableInventory extends ImportHelperInventoryBase<ItemDomainCableInventory, ItemDomainCableInventoryController> {


    private static final Logger LOGGER = LogManager.getLogger(ImportHelperCableInventory.class.getName());
    
    @Override
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new IdOrNameRefColumnSpec(0, "Cable Catalog Item", KEY_CATALOG_ITEM, "setCatalogItem", true, "ID or name of cable catalog item for inventory unit. Name must be unique and prefixed with '#'.", ItemDomainCableCatalogController.getInstance(), ItemDomainCableCatalog.class, null));
        specs.add(new StringColumnSpec(1, "Tag", KEY_NAME, "", true, "Name of inventory unit.", 64));
        specs.add(new IntegerColumnSpec(2, "QR ID", "qrId", "setQrId", false, "Integer QR id of inventory unit."));
        specs.add(new StringColumnSpec(3, "Description", "description", "setDescription", false, "Description of inventory unit.", 256));
        specs.add(statusColumnSpec(4));
        specs.add(locationColumnSpec(5));
        specs.add(locationDetailsColumnSpec(6));
        specs.add(new StringColumnSpec(7, "Length", "length", "setLength", false, "Installed length of cable inventory unit.", 256));
        specs.add(projectListColumnSpec(8));
        specs.add(ownerUserColumnSpec(9));
        specs.add(ownerGroupColumnSpec(10));
        
        return specs;
    } 
   
    @Override
    public ItemDomainCableInventoryController getEntityController() {
        return ItemDomainCableInventoryController.getInstance();
    }

    @Override
    public String getTemplateFilename() {
        return "Cable Inventory Template";
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
