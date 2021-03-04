/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCableDesignControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperCableDesign extends ImportHelperBase<ItemDomainCableDesign, ItemDomainCableDesignController> {
   
    private static final String LABEL_NAME = "Name";

    @Override
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(existingItemIdColumnSpec());
        specs.add(deleteExistingItemColumnSpec());
        specs.add(new StringColumnSpec("Name", "name", "setName", true, "Cable name, uniquely identifies cable.", 128, "getName"));
        specs.add(new StringColumnSpec("Alt Name", "alternateName", "setAlternateName", false, "Alternate cable name. Embedded '#cdbid# tag will be replaced with the internal CDB identifier (integer).", 128, "getAlternateName"));
        specs.add(new StringColumnSpec("Ext Cable Name", "externalCableName", "setExternalCableName", false, "Cable name in external system (e.g., CAD, routing tool).", 256, "getExternalCableName"));
        specs.add(new StringColumnSpec("Import Cable ID", "importCableId", "setImportCableId", false, "Import cable identifier.", 256, "getImportCableId"));
        specs.add(new StringColumnSpec("Alternate Cable ID", "alternateCableId", "setAlternateCableId", false, "Alternate (e.g., group-specific) cable identifier.", 256, "getAlternateCableId"));
        specs.add(new StringColumnSpec("Legacy QR ID", "legacyQrId", "setLegacyQrId", false, "Legacy QR identifier, e.g., for cables that have already been assigned a QR code.", 256, "getLegacyQrId"));
        specs.add(new StringColumnSpec("Description", "description", "setDescription", false, "Description of cable.", 256, "getDescription"));
        specs.add(new StringColumnSpec("Laying", "laying", "setLaying", false, "Laying style e.g., S=single-layer, M=multi-layer, T=triangular, B=bundle", 256, "getLaying"));
        specs.add(new StringColumnSpec("Voltage", "voltage", "setVoltage", false, "Voltage aplication e.g., COM=communication, CTRL=control, IW=instrumentation, LV=low voltage, MV=medium voltage", 256, "getVoltage"));
        specs.add(new IdOrNameRefColumnSpec("Type", "catalogItemString", "setCatalogItem", false, "Numeric ID or name of CDB cable type catalog item. Name must be unique and prefixed with '#'.", ItemDomainCableCatalogController.getInstance(), Item.class, "", "getCatalogItem", false, false));
        specs.add(new IdOrNameRefColumnSpec("Endpoint1", "endpoint1String", "setEndpoint1Import", false, "Numeric ID or name of CDB machine design item for first endpoint. Name must be unique and prefixed with '#'.", ItemDomainMachineDesignController.getInstance(), Item.class, "", "getEndpoint1", false, false));
        specs.add(new StringColumnSpec("Endpoint1 Desc", "endpoint1Description", "setEndpoint1Description", false, "Endpoint details useful for external editing.", 256, "getEndpoint1Description"));
        specs.add(new StringColumnSpec("Endpoint1 Route", "endpoint1Route", "setEndpoint1Route", false, "Routing waypoint for first endpoint.", 256, "getEndpoint1Route"));        
        specs.add(new IdOrNameRefColumnSpec("Endpoint2", "endpoint2String", "setEndpoint2Import", false, "Numeric ID or name of CDB machine design item for second endpoint. Name must be unique and prefixed with '#'.", ItemDomainMachineDesignController.getInstance(), Item.class, "", "getEndpoint2", false, false));
        specs.add(new StringColumnSpec("Endpoint2 Desc", "endpoint2Description", "setEndpoint2Description", false, "Endpoint details useful for external editing.", 256, "getEndpoint2Description"));
        specs.add(new StringColumnSpec("Endpoint2 Route", "endpoint2Route", "setEndpoint2Route", false, "Routing waypoint for second endpoint.", 256, "getEndpoint2Route"));        
        specs.add(projectListColumnSpec());
        specs.add(technicalSystemListColumnSpec(ItemDomainName.cableDesign.getValue()));
        specs.add(ownerUserColumnSpec());
        specs.add(ownerGroupColumnSpec());


        return specs;
    }
    
    @Override
    public ItemDomainCableDesignController getEntityController() {
        return ItemDomainCableDesignController.getInstance();
    }

    @Override
    public boolean supportsModeUpdate() {
        return true;
    }

    @Override
    public boolean supportsModeDelete() {
        return true;
    }

    @Override
    public String getFilenameBase() {
        return "Cable Design";
    }
    
    @Override
    protected ItemDomainCableDesign newInvalidUpdateInstance() {
        return getEntityController().createEntityInstance();
    }

    @Override 
    protected ValidInfo preImport() {
        getEntityController().migrateCoreMetadataPropertyType();
        return new ValidInfo(true, "");
    }
    
    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        ItemDomainCableDesign entity = getEntityController().createEntityInstance();
        return new CreateInfo(entity, true, "");
    }
    
    /**
     * Updates list of items in update mode.  Overridden here to customize by
     * deleting list of cable relationships for endpoints updated to null
     * in import/update process.
     */
    @Override
    protected void updateList() throws CdbException, RuntimeException {
        
        // domain object keeps track of relationships that need to be destroyed,
        // e.g., when an endpoint is set to null in the import spreadsheet.
        // updateList in facade is overridden to also destroy those relationships
        
        UserInfo user = SessionUtility.getUser();
        ItemDomainCableDesignControllerUtility utility = new ItemDomainCableDesignControllerUtility();
        utility.updateList(rows, user);
        
        getEntityController().resetListForView();
    }
}
