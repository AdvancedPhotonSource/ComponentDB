/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableInventoryController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperCableInventory extends ImportHelperBase<ItemDomainCableInventory, ItemDomainCableInventoryController> {


    protected static String completionUrlValue = "/views/itemDomainCableInventory/list?faces-redirect=true";
    
    @Override
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new IdOrNameRefColumnSpec(0, "Cable Catalog Item", "catalogItem", "setCatalogItem", true, "ID or name of cable catalog item for inventory unit", ItemDomainCableCatalogController.getInstance(), ItemDomainCableCatalog.class, null));
        specs.add(new StringColumnSpec(1, "Name", "name", "setName", true, "Name for inventory unit.", 64));
        specs.add(new IntegerColumnSpec(2, "QR ID", "qrId", "setQrId", false, "Integer QR id of inventory unit."));
        specs.add(new StringColumnSpec(3, "Description", "description", "setDescription", false, "Description of inventory unit.", 256));
        specs.add(new StringColumnSpec(4, "Length", "length", "setLength", false, "Installed length of cable inventory unit.", 256));
        
        return specs;
    } 
   
    @Override
    protected String getCompletionUrlValue() {
        return completionUrlValue;
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
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        ItemDomainCableInventory entity = getEntityController().createEntityInstance();
        return new CreateInfo(entity, true, "");
    }  

    protected boolean ignoreDuplicates() {
        return false;
    }
}
