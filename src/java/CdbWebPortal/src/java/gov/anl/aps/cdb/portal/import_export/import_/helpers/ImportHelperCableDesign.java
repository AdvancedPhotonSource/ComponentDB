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
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
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
        
        specs.add(new StringColumnSpec(
                "Name", 
                "name", 
                "setName", 
                "Cable name, uniquely identifies cable.", 
                "getName",
                ColumnModeOptions.rCREATErUPDATE(), 
                128));
        
        specs.add(new StringColumnSpec(
                "Alt Name", 
                "alternateName", 
                "setAlternateName", 
                "Alternate cable name. Embedded '#cdbid# tag will be replaced with the internal CDB identifier (integer).", 
                "getAlternateName",
                ColumnModeOptions.oCREATEoUPDATE(), 
                128));
        
        specs.add(new StringColumnSpec(
                "Ext Cable Name", 
                "externalCableName", 
                "setExternalCableName", 
                "Cable name in external system (e.g., CAD, routing tool).", 
                "getExternalCableName",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Import Cable ID", 
                "importCableId", 
                "setImportCableId", 
                "Import cable identifier.", 
                "getImportCableId",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Alternate Cable ID", 
                "alternateCableId", 
                "setAlternateCableId", 
                "Alternate (e.g., group-specific) cable identifier.", 
                "getAlternateCableId",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Legacy QR ID", 
                "legacyQrId", 
                "setLegacyQrId", 
                "Legacy QR identifier, e.g., for cables that have already been assigned a QR code.", 
                "getLegacyQrId",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Description", 
                "description", 
                "setDescription", 
                "Description of cable.", 
                "getDescription",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Laying", 
                "laying", 
                "setLaying", 
                "Laying style e.g., S=single-layer, M=multi-layer, T=triangular, B=bundle", 
                "getLaying",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Voltage", 
                "voltage", 
                "setVoltage", 
                "Voltage aplication e.g., COM=communication, CTRL=control, IW=instrumentation, LV=low voltage, MV=medium voltage", 
                "getVoltage",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new IdOrNameRefColumnSpec(
                "Type", 
                "catalogItemString", 
                "setCatalogItem", 
                "Numeric ID or name of CDB cable type catalog item. Name must be unique and prefixed with '#'.", 
                "getCatalogItem",
                ColumnModeOptions.oCREATEoUPDATE(), 
                ItemDomainCableCatalogController.getInstance(), 
                Item.class, 
                ""));
        
        specs.add(new IdOrNameRefColumnSpec(
                "Endpoint1", 
                "endpoint1String", 
                "setEndpoint1Import", 
                "Numeric ID or name of CDB machine design item for first endpoint. Name must be unique and prefixed with '#'.", 
                "getEndpoint1",
                ColumnModeOptions.oCREATEoUPDATE(), 
                ItemDomainMachineDesignController.getInstance(), 
                Item.class, 
                ""));
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Desc", 
                "endpoint1Description", 
                "setEndpoint1Description", 
                "Endpoint details useful for external editing.", 
                "getEndpoint1Description",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Route", 
                "endpoint1Route", 
                "setEndpoint1Route", 
                "Routing waypoint for first endpoint.", 
                "getEndpoint1Route",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new IdOrNameRefColumnSpec(
                "Endpoint2", 
                "endpoint2String", 
                "setEndpoint2Import", 
                "Numeric ID or name of CDB machine design item for second endpoint. Name must be unique and prefixed with '#'.", 
                "getEndpoint2",
                ColumnModeOptions.oCREATEoUPDATE(), 
                ItemDomainMachineDesignController.getInstance(), 
                Item.class, 
                ""));
        
        specs.add(new StringColumnSpec(
                "Endpoint2 Desc", 
                "endpoint2Description", 
                "setEndpoint2Description", 
                "Endpoint details useful for external editing.", 
                "getEndpoint2Description",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Endpoint2 Route", 
                "endpoint2Route", 
                "setEndpoint2Route", 
                "Routing waypoint for second endpoint.", 
                "getEndpoint2Route",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
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
