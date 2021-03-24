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
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
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
    
    private static final String KEY_CATALOG_ITEM = "catalogItemString";
    private static final String KEY_ENDPOINT1_ITEM = "endpoint1String";
    private static final String KEY_ENDPOINT1_PORT = "endpoint1Port";
    private static final String KEY_ENDPOINT1_CONNECTOR = "endpoint1Connector";
    private static final String KEY_ENDPOINT1_DESCRIPTION = "endpoint1Description";
    private static final String KEY_ENDPOINT1_ROUTE = "endpoint1Route";
    private static final String KEY_ENDPOINT1_END_LENGTH = "endpoint1EndLength";
    private static final String KEY_ENDPOINT1_TERMINATION = "endpoint1Termination";
    private static final String KEY_ENDPOINT1_PINLIST = "endpoint1Pinlist";
    private static final String KEY_ENDPOINT1_NOTES = "endpoint1Notes";
    private static final String KEY_ENDPOINT1_DRAWING = "endpoint1Drawing";

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
        
        specs.add(new StringColumnSpec(
                "Routed Length", 
                "routedLength", 
                "setRoutedLength", 
                "Routed cable length.", 
                "getRoutedLength",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Route", 
                "route", 
                "setRoute", 
                "Routing information.", 
                "getRoute",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Notes", 
                "notes", 
                "setNotes", 
                "Cable notes.", 
                "getVoltage",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new IdOrNameRefColumnSpec(
                "Type", 
                KEY_CATALOG_ITEM, 
                "", 
                "Numeric ID or name of CDB cable type catalog item. Name must be unique and prefixed with '#'.", 
                "getCatalogItem",
                ColumnModeOptions.oCREATEoUPDATE(), 
                ItemDomainCableCatalogController.getInstance(), 
                Item.class, 
                ""));
        
        specs.add(new IdOrNameRefColumnSpec(
                "Endpoint1", 
                KEY_ENDPOINT1_ITEM, 
                "", 
                "Numeric ID or name of CDB machine design item for first endpoint. Name must be unique and prefixed with '#'.", 
                "getEndpoint1",
                ColumnModeOptions.oCREATEoUPDATE(), 
                ItemDomainMachineDesignController.getInstance(), 
                Item.class, 
                ""));
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Port", 
                KEY_ENDPOINT1_PORT, 
                "", 
                "Port name on device for endpoint1 connection.", 
                "getEndpoint1Description",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Connector", 
                KEY_ENDPOINT1_CONNECTOR, 
                "", 
                "Cable connector name for endpoint1 connection.", 
                "getEndpoint1Connector",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Desc", 
                KEY_ENDPOINT1_DESCRIPTION, 
                "setEndpoint1Description", 
                "Endpoint1 description.", 
                "getEndpoint1Description",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Route", 
                KEY_ENDPOINT1_ROUTE, 
                "setEndpoint1Route", 
                "Routing waypoint for endpoint1.", 
                "getEndpoint1Route",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint1 End Length", 
                KEY_ENDPOINT1_END_LENGTH, 
                "setEndpoint1EndLength", 
                "End length for endpoint1.", 
                "getEndpoint1EndLength",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Termination", 
                KEY_ENDPOINT1_TERMINATION, 
                "setEndpoint1Termination", 
                "Termination for endpoint1.", 
                "getEndpoint1Termination",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Pinlist", 
                KEY_ENDPOINT1_PINLIST, 
                "setEndpoint1Pinlist", 
                "Pinlist for endpoint1.", 
                "getEndpoint1Pinlist",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Notes", 
                KEY_ENDPOINT1_NOTES, 
                "setEndpoint1Notes", 
                "Notes for endpoint1.", 
                "getEndpoint1Notes",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Drawing", 
                KEY_ENDPOINT1_DRAWING, 
                "setEndpoint1Drawing", 
                "Drawing for endpoint1.", 
                "getEndpoint1Drawing",
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
        getEntityController().createOrMigrateCoreMetadataPropertyType();
        getEntityController().createOrMigrateConnectionPropertyType();
        return new ValidInfo(true, "");
    }
    
    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        
        boolean isValid = true;
        String validString = "";
        
        ItemDomainCableDesign entity = getEntityController().createEntityInstance();
        
        // set cable type so connectors inherited from cable type are synced in setEndpointImport()
        ItemDomainCableCatalog catalogItem = (ItemDomainCableCatalog) rowMap.get(KEY_CATALOG_ITEM);
        if (catalogItem != null) {
            entity.setCatalogItem(catalogItem);
        }
        
        // handle endpoint1 ======
        
        ItemDomainMachineDesign endpoint1Item = (ItemDomainMachineDesign) rowMap.get(KEY_ENDPOINT1_ITEM);
        String endpoint1PortName = (String) rowMap.get(KEY_ENDPOINT1_PORT);
        String endpoint1ConnectorName = (String) rowMap.get(KEY_ENDPOINT1_CONNECTOR);
        
        // shouldn't specify endpoint properties if endpoint is not specified
        if (endpoint1Item == null) {
            String endpoint1Description = (String) rowMap.get(KEY_ENDPOINT1_DESCRIPTION);
            String endpoint1Route = (String) rowMap.get(KEY_ENDPOINT1_ROUTE);
            String endpoint1EndLength = (String) rowMap.get(KEY_ENDPOINT1_END_LENGTH);
            String endpoint1Termination = (String) rowMap.get(KEY_ENDPOINT1_TERMINATION);
            String endpoint1Pinlist = (String) rowMap.get(KEY_ENDPOINT1_PINLIST);
            String endpoint1Notes = (String) rowMap.get(KEY_ENDPOINT1_NOTES);
            String endpoint1Drawing = (String) rowMap.get(KEY_ENDPOINT1_DRAWING);
            if ((endpoint1PortName != null)
                    || (endpoint1ConnectorName != null)
                    || (endpoint1Description != null)
                    || (endpoint1Route != null)
                    || (endpoint1EndLength != null)
                    || (endpoint1Termination != null)
                    || (endpoint1Pinlist != null)
                    || (endpoint1Notes != null)
                    || (endpoint1Drawing != null)) {
                
                isValid = false;
                validString = appendToString(
                        validString, "Endpoint1 properties cannot be specified if endpoint1 is not specified.");
                
            }
        } else {
            ValidInfo endpoint1ValidInfo =
                    entity.setEndpoint1Import(endpoint1Item, endpoint1PortName, endpoint1ConnectorName);
            if (!endpoint1ValidInfo.isValid()) {
                isValid = false;
                validString = appendToString(
                        validString, endpoint1ValidInfo.getValidString());
            }
        }
        
        return new CreateInfo(entity, isValid, validString);
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
