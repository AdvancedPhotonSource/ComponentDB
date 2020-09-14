/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainLocationController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignInventoryController;
import gov.anl.aps.cdb.portal.controllers.ItemProjectController;
import gov.anl.aps.cdb.portal.controllers.LocatableItemController;
import gov.anl.aps.cdb.portal.controllers.UserGroupController;
import gov.anl.aps.cdb.portal.controllers.UserInfoController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefListColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IntegerColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperMachineInventory extends ImportHelperBase<ItemDomainMachineDesign, ItemDomainMachineDesignInventoryController> {
    
    private static final String KEY_TEMPLATE = "importMdItem";
    private static final String KEY_NAME = "name";
    private static final String KEY_LOCATION = "locationItem";
    private static final String AUTO_VALUE = "auto";
    
    @Override
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new IdOrNameRefColumnSpec(0, "MD Template", KEY_TEMPLATE, "setImportMdItem", true, "ID or name of CDB machine design template.", ItemDomainMachineDesignController.getInstance(), ItemDomainMachineDesign.class, null));
        specs.add(new StringColumnSpec(1, "Name", KEY_NAME, "", true, "Name for inventory unit (use 'auto' to generate automatically).", 128));
        specs.add(new StringColumnSpec(2, "Alt Name", "alternateName", "setAlternateName", false, "Alternate name for inventory unit.", 128));
        specs.add(new IntegerColumnSpec(3, "QR ID", "qrId", "setQrId", false, "Integer QR id of inventory unit."));
        specs.add(new StringColumnSpec(4, "Description", "description", "setDescription", false, "Description of inventory unit.", 256));
        specs.add(new StringColumnSpec(5, "Status", "inventoryStatusValue", "setInventoryStatusValue", false, "Status of inventory item.", 256));
        specs.add(new IdOrNameRefColumnSpec(6, "Location", KEY_LOCATION, "setLocation", false, "Inventory unit location.", ItemDomainLocationController.getInstance(), ItemDomainLocation.class, ""));
        specs.add(new StringColumnSpec(7, "Location Details", "locationDetails", "setLocationDetails", false, "Location details for inventory unit.", 256));
        specs.add(new IdOrNameRefListColumnSpec(8, "Project", "itemProjectString", "setItemProjectList", true, "Comma-separated list of IDs of CDB project(s).", ItemProjectController.getInstance(), List.class, ""));
        specs.add(new IdOrNameRefColumnSpec(9, "Owner User", "ownerUserName", "setOwnerUser", false, "ID or name of CDB owner user.", UserInfoController.getInstance(), UserInfo.class, ""));
        specs.add(new IdOrNameRefColumnSpec(10, "Owner Group", "ownerUserGroupName", "setOwnerUserGroup", false, "ID or name of CDB owner user group.", UserGroupController.getInstance(), UserGroup.class, ""));
        
        return specs;
    } 
   
    @Override
    public ItemDomainMachineDesignInventoryController getEntityController() {
        return ItemDomainMachineDesignInventoryController.getInstance();
    }

    @Override
    public String getTemplateFilename() {
        return "Machine Inventory Template";
    }
    
    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {

        String methodLogName = "createEntityFromTemplateInstantiation() ";
        boolean isValid = true;
        String validString = "";
        
        ItemDomainMachineDesign template = (ItemDomainMachineDesign) rowMap.get(KEY_TEMPLATE);
        ItemDomainMachineDesign item = null;
        if (template != null) {
            item = getEntityController().performPrepareCreateInventoryFromTemplate(template);
        }
        
        if (item == null) {
            isValid = false;
            validString = "controller failed to create inventory item";
            item = getEntityController().createEntityInstance();
        } else {
            // prepare to update location info?
            LocatableItemController.getInstance().setItemLocationInfo(item);
            
            // override item name if not "auto"
            String itemName = (String) rowMap.get(KEY_NAME);
            if (!itemName.equals(AUTO_VALUE)) {
                item.setName(itemName);
            }
        }
        
        return new CreateInfo(item, isValid, validString);
    }
    
}
