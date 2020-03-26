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
        columns.add(new ImportHelperBase.StringColumnModel("Name", "name", "setName", true, "Cable name, uniquely identifies cable in CDB."));
        columns.add(new ImportHelperBase.StringColumnModel("Alt Name", "alternateName", "setAlternateName", true, "Alternate cable name."));
        columns.add(new ImportHelperBase.StringColumnModel("Ext Cable Name", "externalCableName", "setExternalCableName", true, "Cable name in external system (e.g., CAD, routing tool) e.g., SR_R_401_D1109_RR8G[low] | SR_M_A02_C61_64_02-00[high]"));
        columns.add(new ImportHelperBase.StringColumnModel("Import Cable ID", "importCableId", "setImportCableId", true, "Unique identifier for imported cables."));
        columns.add(new ImportHelperBase.StringColumnModel("Alternate Cable ID", "alternateCableId", "setAlternateCableId", true, "Alternate (e.g., group-specific) cable identifier."));
        columns.add(new ImportHelperBase.StringColumnModel("Description", "description", "setDescription", true, "Description of cable."));
        columns.add(new ImportHelperBase.StringColumnModel("Laying", "laying", "setLaying", false, "Laying style e.g., S=single-layer, M=multi-layer, T=triangular, B=bundle"));
        columns.add(new ImportHelperBase.StringColumnModel("Voltage", "voltage", "setVoltage", false, "Voltage aplication e.g., COM=communication, CTRL=control, IW=instrumentation, LV=low voltage, MV=medium voltage"));
        columns.add(new ImportHelperBase.IdRefColumnModel("Owner", "team", "setTeamId", true, "Numeric ID of CDB technical system.", ItemCategoryController.getInstance()));
        columns.add(new ImportHelperBase.IdRefColumnModel("Project", "itemProjectString", "setProjectId", true, "Numeric ID of CDB project.", ItemProjectController.getInstance()));
        columns.add(new ImportHelperBase.IdRefColumnModel("Type", "catalogItemString", "setCatalogItemId", true, "Numeric ID of CDB cable type catalog item.", ItemDomainCableCatalogController.getInstance()));
        columns.add(new ImportHelperBase.IdRefColumnModel("Endpoint1", "endpoint1String", "setEndpoint1Id", true, "Numeric ID of CDB machine design item for first endpoint.", ItemDomainMachineDesignController.getInstance()));
        columns.add(new ImportHelperBase.IdRefColumnModel("Endpoint2", "endpoint2String", "setEndpoint2Id", true, "Numeric ID of CDB machine design item for second endpoint.", ItemDomainMachineDesignController.getInstance()));
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
