/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainLocationController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemProjectController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;

/**
 *
 * @author craig
 */
public class ImportHelperMachineDesign extends ImportHelperBase<ItemDomainMachineDesign, ItemDomainMachineDesignController> {


    protected static String completionUrlValue = "/views/itemDomainMachineDesign/list?faces-redirect=true";
    
    @Override
    protected void createColumnModels_() {
        columns.add(new ImportHelperBase.StringColumnModel("Is Template", "importIsTemplate", "setImportIsTemplate", true, "Specifies whether this item is a template (true or false).", 5));
        columns.add(new ImportHelperBase.IdRefColumnModel("Container Id", "importContainerString", "setImportContainerItemId", true, "Numeric ID of CDB machine design item that contains this new machine design hierarchy.", 0, ItemDomainMachineDesignController.getInstance()));
        columns.add(new ImportHelperBase.StringColumnModel("Path", "importPath", "setImportPath", false, "Path to new machine design item within container (/ separated). If path is empty, new item will be created directly in specified container.", 700));
        columns.add(new ImportHelperBase.StringColumnModel("Name", "name", "setName", true, "Machine design item name.", 128));
        columns.add(new ImportHelperBase.StringColumnModel("Alt Name", "alternateName", "setAlternateName", false, "Alternate machine design item name.", 32));
        columns.add(new ImportHelperBase.StringColumnModel("Description", "description", "setDescription", false, "Textual description of machine design item.", 256));
        columns.add(new ImportHelperBase.IdRefColumnModel("Assigned Catalog Item Id", "importAssignedCatalogItemString", "setAssignedCatalogItemId", false, "Numeric ID of assigned catalog item.", 0, ItemDomainCatalogController.getInstance()));
        columns.add(new ImportHelperBase.IdRefColumnModel("Assigned Inventory Item Id", "importAssignedInventoryItemString", "setAssignedInventoryItemId", false, "Numeric ID of assigned inventory item.", 0, ItemDomainInventoryController.getInstance()));
        columns.add(new ImportHelperBase.StringColumnModel("Location", "locationString", "setLocationString", false, "CDB location string.", 0));
        columns.add(new ImportHelperBase.IdRefColumnModel("Project", "itemProjectString", "setProjectId", true, "Numeric ID of CDB project.", 0, ItemProjectController.getInstance()));
    }
    
    @Override
    protected String getCompletionUrlValue() {
        return completionUrlValue;
    }
    
    @Override
    public ItemDomainMachineDesignController getEntityController() {
        return ItemDomainMachineDesignController.getInstance();
    }

    @Override
    public String getTemplateFilename() {
        return "Machine Design Template";
    }
    
    @Override
    protected ItemDomainMachineDesign createEntityInstance() {
        return getEntityController().createEntityInstance();
    }
}