/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainLocationController;
import gov.anl.aps.cdb.portal.controllers.ItemProjectController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
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
        
        specs.add(new ImportHelperBase.IdOrNameRefColumnSpec(0, "Catalog Item", KEY_CATALOG_ITEM, "setCatalogItem", true, "ID or name of catalog item for inventory unit", ItemDomainCatalogController.getInstance(), ItemDomainCatalog.class, null));
        specs.add(new ImportHelperBase.StringColumnSpec(1, "Tag", KEY_NAME, "", true, "Name for inventory unit.", 64));
        specs.add(new ImportHelperBase.IntegerColumnSpec(2, "QR ID", "qrId", "setQrId", false, "Integer QR id of inventory unit."));
        specs.add(new ImportHelperBase.StringColumnSpec(3, "Serial Number", "serialNumber", "setSerialNumber", false, "Inventory unit serial number.", 128));
        specs.add(new ImportHelperBase.StringColumnSpec(4, "Description", "description", "setDescription", false, "Description of inventory unit.", 256));
        specs.add(new ImportHelperBase.StringColumnSpec(5, "Status", KEY_STATUS, "setInventoryStatusValue", false, "Status of inventory item.", 256));
        specs.add(new ImportHelperBase.IdOrNameRefColumnSpec(6, "Location", "importLocationItemString", "setImportLocationItem", false, "Inventory unit location.", ItemDomainLocationController.getInstance(), ItemDomainLocation.class, ""));
        specs.add(new ImportHelperBase.StringColumnSpec(7, "Location Details", "locationDetails", "setLocationDetails", false, "Location details for inventory unit.", 256));
        specs.add(new ImportHelperBase.IdOrNameRefColumnSpec(8, "Project", "itemProjectString", "setProject", true, "Numeric ID of CDB project.", ItemProjectController.getInstance(), ItemProject.class, ""));
        
        return specs;
    } 
   
    @Override
    public ItemDomainInventoryController getEntityController() {
        return ItemDomainInventoryController.getInstance();
    }

    @Override
    public String getTemplateFilename() {
        return "Component Inventory Template";
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
