/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableInventoryController;
import gov.anl.aps.cdb.portal.controllers.ItemProjectController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import java.util.ArrayList;
import java.util.HashMap;
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
        
        specs.add(new IdOrNameRefColumnSpec(0, "Cable Catalog Item", KEY_CATALOG_ITEM, "setCatalogItem", true, "ID or name of cable catalog item for inventory unit", ItemDomainCableCatalogController.getInstance(), ItemDomainCableCatalog.class, null));
        specs.add(new StringColumnSpec(1, "Name", KEY_NAME, "", true, "Name for inventory unit.", 64));
        specs.add(new IntegerColumnSpec(2, "QR ID", "qrId", "setQrId", false, "Integer QR id of inventory unit."));
        specs.add(new StringColumnSpec(3, "Description", "description", "setDescription", false, "Description of inventory unit.", 256));
        specs.add(new IdOrNameRefColumnSpec(4, "Project", "itemProjectString", "setProject", true, "Numeric ID of CDB project.", ItemProjectController.getInstance(), ItemProject.class, ""));
        specs.add(new StringColumnSpec(5, "Status", KEY_STATUS, "setInventoryStatusValue", false, "Status of inventory item.", 256));
        specs.add(new StringColumnSpec(6, "Length", "length", "setLength", false, "Installed length of cable inventory unit.", 256));
        
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
