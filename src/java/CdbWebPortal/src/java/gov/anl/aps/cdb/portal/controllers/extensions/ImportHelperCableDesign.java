/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemCategoryController;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemProjectController;

/**
 *
 * @author craig
 */
public class ImportHelperCableDesign extends ImportHelperBase {


    protected static String completionUrlValue = "/views/itemDomainCableDesign/list?faces-redirect=true";
    
    @Override
    protected void createColumnModels_() {
        columns.add(new ImportHelperBase.StringColumnModel("Name", "name", "setName", true, "CCG cable 1"));
        columns.add(new ImportHelperBase.StringColumnModel("Laying", "laying", "setLaying", false, "S"));
        columns.add(new ImportHelperBase.StringColumnModel("Voltage", "voltage", "setVoltage", false, "COM"));
        columns.add(new ImportHelperBase.IdRefColumnModel("Owner", "team", "setTeamId", true, "1234", ItemCategoryController.getInstance()));
        columns.add(new ImportHelperBase.IdRefColumnModel("Project", "itemProjectString", "setProjectId", true, "1234", ItemProjectController.getInstance()));
        columns.add(new ImportHelperBase.IdRefColumnModel("Type", "catalogItemString", "setCatalogItemId", true, "1234", ItemDomainCableCatalogController.getInstance()));
        columns.add(new ImportHelperBase.IdRefColumnModel("Endpoint1", "endpoint1String", "setEndpoint1Id", true, "1234", ItemDomainMachineDesignController.getInstance()));
        columns.add(new ImportHelperBase.IdRefColumnModel("Endpoint2", "endpoint2String", "setEndpoint2Id", true, "1234", ItemDomainMachineDesignController.getInstance()));
    }
    
    @Override
    protected String getCompletionUrlValue() {
        return completionUrlValue;
    }
    
    @Override
    public int getDataStartRow() {
        return 1;
    }

    @Override
    public ItemController getEntityController() {
        return ItemDomainCableDesignController.getInstance();
    }

    @Override
    public String getTemplateFilename() {
        return "Cable Design Template";
    }
}
