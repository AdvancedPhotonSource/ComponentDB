/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
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
    private static final String KEY_ENDPOINT1_DESCRIPTION = "descriptionEndpoint1";
    private static final String KEY_ENDPOINT1_ROUTE = "routeEndpoint1";
    private static final String KEY_ENDPOINT1_END_LENGTH = "endLengthEndpoint1";
    private static final String KEY_ENDPOINT1_TERMINATION = "terminationEndpoint1";
    private static final String KEY_ENDPOINT1_PINLIST = "pinlistEndpoint1";
    private static final String KEY_ENDPOINT1_NOTES = "notesEndpoint1";
    private static final String KEY_ENDPOINT1_DRAWING = "drawingEndpoint1";

    private static final String KEY_ENDPOINT2_ITEM = "endpoint2String";
    private static final String KEY_ENDPOINT2_PORT = "endpoint2Port";
    private static final String KEY_ENDPOINT2_CONNECTOR = "endpoint2Connector";
    private static final String KEY_ENDPOINT2_DESCRIPTION = "descriptionEndpoint2";
    private static final String KEY_ENDPOINT2_ROUTE = "routeEndpoint2";
    private static final String KEY_ENDPOINT2_END_LENGTH = "endLengthEndpoint2";
    private static final String KEY_ENDPOINT2_TERMINATION = "terminationEndpoint2";
    private static final String KEY_ENDPOINT2_PINLIST = "pinlistEndpoint2";
    private static final String KEY_ENDPOINT2_NOTES = "notesEndpoint2";
    private static final String KEY_ENDPOINT2_DRAWING = "drawingEndpoint2";

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
                "getNotes",
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
                "Numeric ID or name of CDB machine design item for endpoint1. Name must be unique and prefixed with '#'.", 
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
                "getEndpoint1Port",
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
                "setDescriptionEndpoint1", 
                "Endpoint1 description.", 
                "getDescriptionEndpoint1",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Route", 
                KEY_ENDPOINT1_ROUTE, 
                "setRouteEndpoint1", 
                "Routing waypoint for endpoint1.", 
                "getRouteEndpoint1",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint1 End Length", 
                KEY_ENDPOINT1_END_LENGTH, 
                "setEndLengthEndpoint1", 
                "End length for endpoint1.", 
                "getEndLengthEndpoint1",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Termination", 
                KEY_ENDPOINT1_TERMINATION, 
                "setTerminationEndpoint1", 
                "Termination for endpoint1.", 
                "getTerminationEndpoint1",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Pinlist", 
                KEY_ENDPOINT1_PINLIST, 
                "setPinlistEndpoint1", 
                "Pinlist for endpoint1.", 
                "getPinlistEndpoint1",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Notes", 
                KEY_ENDPOINT1_NOTES, 
                "setNotesEndpoint1", 
                "Notes for endpoint1.", 
                "getNotesEndpoint1",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Drawing", 
                KEY_ENDPOINT1_DRAWING, 
                "setDrawingEndpoint1", 
                "Drawing for endpoint1.", 
                "getDrawingEndpoint1",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new IdOrNameRefColumnSpec(
                "Endpoint2", 
                KEY_ENDPOINT2_ITEM, 
                "", 
                "Numeric ID or name of CDB machine design item for endpoint2. Name must be unique and prefixed with '#'.", 
                "getEndpoint2",
                ColumnModeOptions.oCREATEoUPDATE(), 
                ItemDomainMachineDesignController.getInstance(), 
                Item.class, 
                ""));
        
        specs.add(new StringColumnSpec(
                "Endpoint2 Port", 
                KEY_ENDPOINT2_PORT, 
                "", 
                "Port name on device for endpoint2 connection.", 
                "getEndpoint2Port",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Endpoint2 Connector", 
                KEY_ENDPOINT2_CONNECTOR, 
                "", 
                "Cable connector name for endpoint2 connection.", 
                "getEndpoint2Connector",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Endpoint2 Desc", 
                KEY_ENDPOINT2_DESCRIPTION, 
                "setDescriptionEndpoint2", 
                "Endpoint2 description.", 
                "getDescriptionEndpoint2",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Endpoint2 Route", 
                KEY_ENDPOINT2_ROUTE, 
                "setRouteEndpoint2", 
                "Routing waypoint for endpoint2.", 
                "getRouteEndpoint2",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint2 End Length", 
                KEY_ENDPOINT2_END_LENGTH, 
                "setEndLengthEndpoint2", 
                "End length for endpoint2.", 
                "getEndLengthEndpoint2",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint2 Termination", 
                KEY_ENDPOINT2_TERMINATION, 
                "setTerminationEndpoint2", 
                "Termination for endpoint2.", 
                "getTerminationEndpoint2",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint2 Pinlist", 
                KEY_ENDPOINT2_PINLIST, 
                "setPinlistEndpoint2", 
                "Pinlist for endpoint2.", 
                "getPinlistEndpoint2",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint2 Notes", 
                KEY_ENDPOINT2_NOTES, 
                "setNotesEndpoint2", 
                "Notes for endpoint2.", 
                "getNotesEndpoint2",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint2 Drawing", 
                KEY_ENDPOINT2_DRAWING, 
                "setDrawingEndpoint2", 
                "Drawing for endpoint2.", 
                "getDrawingEndpoint2",
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
        getEntityController().createOrMigrateEndPropertyType();
        return new ValidInfo(true, "");
    }
    
    private ValidInfo createUpdateEntityCommon(
            ItemDomainCableDesign entity,
            Map<String, Object> rowMap) {

        boolean isValid = true;
        String validString = "";
        
        // set cable type so connectors inherited from cable type are synced in setEndpointImport()
        ItemDomainCableCatalog catalogItem = (ItemDomainCableCatalog) rowMap.get(KEY_CATALOG_ITEM);
        entity.setCatalogItem(catalogItem);
        
        // get endpoint items
        ItemDomainMachineDesign endpoint1Item = (ItemDomainMachineDesign) rowMap.get(KEY_ENDPOINT1_ITEM);
        ItemDomainMachineDesign endpoint2Item = (ItemDomainMachineDesign) rowMap.get(KEY_ENDPOINT2_ITEM);        
        
        // check that port names are different if for same device
        String endpoint1PortName = (String) rowMap.get(KEY_ENDPOINT1_PORT);
        String endpoint2PortName = (String) rowMap.get(KEY_ENDPOINT2_PORT);
        if ((endpoint1Item != null)
                && (endpoint1Item == endpoint2Item)
                && (endpoint1PortName != null)
                && (endpoint2PortName != null)
                && (endpoint1PortName.equals(endpoint2PortName))) {
            isValid = false;
            validString = "Port names for same device cannot specify same value: " + endpoint1PortName;
            return new ValidInfo(isValid, validString);                   
        }
        
        // check that cable connector names are different
        String endpoint1ConnectorName = (String) rowMap.get(KEY_ENDPOINT1_CONNECTOR);
        String endpoint2ConnectorName = (String) rowMap.get(KEY_ENDPOINT2_CONNECTOR);
        if ((endpoint1ConnectorName != null) 
                && (endpoint2ConnectorName != null) 
                && (endpoint1ConnectorName.equals(endpoint2ConnectorName))) {
            isValid = false;
            validString = "Cable connector names cannot specify same value: " + endpoint1ConnectorName;
            return new ValidInfo(isValid, validString);                   
        }
        
        // handle endpoint1 ======        
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
        
        // handle endpoint2 ======        
        // shouldn't specify endpoint properties if endpoint is not specified
        if (endpoint2Item == null) {
            String endpoint2Description = (String) rowMap.get(KEY_ENDPOINT2_DESCRIPTION);
            String endpoint2Route = (String) rowMap.get(KEY_ENDPOINT2_ROUTE);
            String endpoint2EndLength = (String) rowMap.get(KEY_ENDPOINT2_END_LENGTH);
            String endpoint2Termination = (String) rowMap.get(KEY_ENDPOINT2_TERMINATION);
            String endpoint2Pinlist = (String) rowMap.get(KEY_ENDPOINT2_PINLIST);
            String endpoint2Notes = (String) rowMap.get(KEY_ENDPOINT2_NOTES);
            String endpoint2Drawing = (String) rowMap.get(KEY_ENDPOINT2_DRAWING);
            if ((endpoint2PortName != null)
                    || (endpoint2ConnectorName != null)
                    || (endpoint2Description != null)
                    || (endpoint2Route != null)
                    || (endpoint2EndLength != null)
                    || (endpoint2Termination != null)
                    || (endpoint2Pinlist != null)
                    || (endpoint2Notes != null)
                    || (endpoint2Drawing != null)) {
                
                isValid = false;
                validString = appendToString(
                        validString, "Endpoint2 properties cannot be specified if endpoint2 is not specified.");
                
            }
        } else {
            ValidInfo endpoint2ValidInfo =
                    entity.setEndpoint2Import(endpoint2Item, endpoint2PortName, endpoint2ConnectorName);
            if (!endpoint2ValidInfo.isValid()) {
                isValid = false;
                validString = appendToString(
                        validString, endpoint2ValidInfo.getValidString());
            }
        }
        
        return new ValidInfo(isValid, validString);
    }
    
    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        
        
        ItemDomainCableDesign entity = getEntityController().createEntityInstance();
        
        ValidInfo validInfo = createUpdateEntityCommon(entity, rowMap);
        
        return new CreateInfo(entity, validInfo);
    }
    
    @Override
    protected ValidInfo updateEntityInstance(ItemDomainCableDesign entity, Map<String, Object> rowMap) {        
        return createUpdateEntityCommon(entity, rowMap);
    }
    
}
