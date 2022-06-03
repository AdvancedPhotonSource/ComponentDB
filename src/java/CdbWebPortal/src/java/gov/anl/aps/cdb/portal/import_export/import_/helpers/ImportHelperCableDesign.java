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
import static gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperBase.KEY_USER;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.BooleanColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperCableDesign extends ImportHelperBase<ItemDomainCableDesign, ItemDomainCableDesignController> {
   
    private static final String LABEL_NAME = "Name";
    
    private static final String HEADER_CATALOG_ITEM = "Type";
    private static final String HEADER_ASSIGNED_INVENTORY_TAG = "Assigned Inventory Tag";
    private static final String HEADER_INSTALLED = "Is Installed";

    private static final String KEY_CATALOG_ITEM = "catalogItemString";
    private static final String KEY_ASSIGNED_INVENTORY_TAG = "importAssignedInventoryTag";
    private static final String KEY_INSTALLED = "importIsInstalled";

    private static final String KEY_ENDPOINT1_ITEM = "endpoint1ItemStringImport";
    private static final String KEY_ENDPOINT1_PORT = "endpoint1PortImport";
    private static final String KEY_ENDPOINT1_CONNECTOR = "endpoint1ConnectorImport";

    private static final String KEY_ENDPOINT2_ITEM = "endpoint2ItemImport";
    private static final String KEY_ENDPOINT2_PORT = "endpoint2PortImport";
    private static final String KEY_ENDPOINT2_CONNECTOR = "endpoint2ConnectorImport";
    
    private Map<ItemDomainCableCatalog, List<String>> cableTypeInventoryTagMap = new HashMap<>();

    @Override
    protected List<ColumnSpec> initColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(existingItemIdColumnSpec());
        specs.add(deleteExistingItemColumnSpec());
        
        specs.add(new StringColumnSpec(
                "Name", 
                "name", 
                "setName", 
                "Cable name, uniquely identifies cable.", 
                "getName",
                ColumnModeOptions.rdCREATErUPDATE(), 
                128));
        
        specs.add(new IdOrNameRefColumnSpec(
                HEADER_CATALOG_ITEM, 
                KEY_CATALOG_ITEM, 
                "", 
                "Numeric ID or name of CDB cable type catalog item. Name must be unique and prefixed with '#'.", 
                "getCatalogItem", 
                "getCatalogItemAttributeMap",
                ColumnModeOptions.oCREATEoUPDATE(), 
                ItemDomainCableCatalogController.getInstance(), 
                Item.class, 
                ""));
        
        specs.add(new StringColumnSpec(
                HEADER_ASSIGNED_INVENTORY_TAG,
                KEY_ASSIGNED_INVENTORY_TAG,
                "setImportAssignedInventoryTag",
                "Name/tag of assigned inventory item.",
                "getImportAssignedInventoryTag",
                ColumnModeOptions.oCREATEoUPDATE(),
                256));
        
        specs.add(new BooleanColumnSpec(
                HEADER_INSTALLED,
                KEY_INSTALLED,
                "setImportIsInstalled",
                "Specify true/false for item with assigned inventory. Leave blank otherwise.",
                "getImportIsInstalled",
                ColumnModeOptions.oCREATEoUPDATE()));

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
                "Endpoint1", 
                KEY_ENDPOINT1_ITEM, 
                "setEndpoint1ItemImport", 
                "Numeric ID or name of CDB machine design item for endpoint1. Name must be unique and prefixed with '#'.", 
                "getEndpoint1", 
                "getEndpoint1AttributeMap",
                ColumnModeOptions.oCREATEoUPDATE(), 
                ItemDomainMachineDesignController.getInstance(), 
                Item.class, 
                ""));
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Port", 
                KEY_ENDPOINT1_PORT, 
                "setEndpoint1PortImport", 
                "Port name on device for endpoint1 connection.", 
                "getEndpoint1Port",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Connector", 
                KEY_ENDPOINT1_CONNECTOR, 
                "setEndpoint1ConnectorImport", 
                "Cable connector name for endpoint1 connection.", 
                "getEndpoint1Connector",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Desc", 
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END1_DESCRIPTION_KEY, 
                "setEndpoint1Description", 
                "Endpoint1 description.", 
                "getEndpoint1Description",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Route", 
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END1_ROUTE_KEY, 
                "setEndpoint1Route", 
                "Routing waypoint for endpoint1.", 
                "getEndpoint1Route",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint1 End Length", 
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END1_ENDLENGTH_KEY, 
                "setEndpoint1EndLength", 
                "End length for endpoint1.", 
                "getEndpoint1EndLength",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Termination", 
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END1_TERMINATION_KEY, 
                "setEndpoint1Termination", 
                "Termination for endpoint1.", 
                "getEndpoint1Termination",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Pinlist", 
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END1_PINLIST_KEY, 
                "setEndpoint1Pinlist", 
                "Pinlist for endpoint1.", 
                "getEndpoint1Pinlist",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Notes", 
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END1_NOTES_KEY, 
                "setEndpoint1Notes", 
                "Notes for endpoint1.", 
                "getEndpoint1Notes",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint1 Drawing", 
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END1_DRAWING_KEY, 
                "setEndpoint1Drawing", 
                "Drawing for endpoint1.", 
                "getEndpoint1Drawing",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new IdOrNameRefColumnSpec(
                "Endpoint2", 
                KEY_ENDPOINT2_ITEM, 
                "setEndpoint2ItemImport", 
                "Numeric ID or name of CDB machine design item for endpoint2. Name must be unique and prefixed with '#'.", 
                "getEndpoint2", 
                "getEndpoint2AttributeMap",
                ColumnModeOptions.oCREATEoUPDATE(), 
                ItemDomainMachineDesignController.getInstance(), 
                Item.class, 
                ""));
        
        specs.add(new StringColumnSpec(
                "Endpoint2 Port", 
                KEY_ENDPOINT2_PORT, 
                "setEndpoint2PortImport", 
                "Port name on device for endpoint2 connection.", 
                "getEndpoint2Port",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Endpoint2 Connector", 
                KEY_ENDPOINT2_CONNECTOR, 
                "setEndpoint2ConnectorImport", 
                "Cable connector name for endpoint2 connection.", 
                "getEndpoint2Connector",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Endpoint2 Desc", 
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END2_DESCRIPTION_KEY, 
                "setEndpoint2Description", 
                "Endpoint2 description.", 
                "getEndpoint2Description",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Endpoint2 Route", 
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END2_ROUTE_KEY, 
                "setEndpoint2Route", 
                "Routing waypoint for endpoint2.", 
                "getEndpoint2Route",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint2 End Length", 
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END2_ENDLENGTH_KEY, 
                "setEndpoint2EndLength", 
                "End length for endpoint2.", 
                "getEndpoint2EndLength",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint2 Termination", 
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END2_TERMINATION_KEY, 
                "setEndpoint2Termination", 
                "Termination for endpoint2.", 
                "getEndpoint2Termination",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint2 Pinlist", 
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END2_PINLIST_KEY, 
                "setEndpoint2Pinlist", 
                "Pinlist for endpoint2.", 
                "getEndpoint2Pinlist",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint2 Notes", 
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END2_NOTES_KEY, 
                "setEndpoint2Notes", 
                "Notes for endpoint2.", 
                "getEndpoint2Notes",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));  
        
        specs.add(new StringColumnSpec(
                "Endpoint2 Drawing", 
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END2_DRAWING_KEY, 
                "setEndpoint2Drawing", 
                "Drawing for endpoint2.", 
                "getEndpoint2Drawing",
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
    public boolean supportsModeTransfer() {
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
        return new ValidInfo(true, "");
    }
    
    private ValidInfo createUpdateEntityCommon(
            ItemDomainCableDesign entity,
            Map<String, Object> rowMap) {

        boolean isValid = true;
        String validString = "";
        
        UserInfo user = (UserInfo) rowMap.get(KEY_USER);
        
        // set cable type so connectors inherited from cable type are synced in setEndpointImport()
        ItemDomainCableCatalog catalogItem = (ItemDomainCableCatalog) rowMap.get(KEY_CATALOG_ITEM);
        entity.setCatalogItem(catalogItem);
        
        // handle assigned inventory and installed status
        String assignedInventoryTag = (String) rowMap.get(KEY_ASSIGNED_INVENTORY_TAG);
        Boolean isInstalled = (Boolean) rowMap.get(KEY_INSTALLED);
        if (assignedInventoryTag != null && catalogItem == null) {
            // must specify catalog item to assign inventory
            isValid = false;
            validString = "'" + HEADER_ASSIGNED_INVENTORY_TAG + "' cannot be specified unless '"
                    + HEADER_CATALOG_ITEM + "' is specified";
            return new ValidInfo(isValid, validString);
        }
        if (isInstalled != null) {
            // must specify assigned inventory to use installation status
            if (assignedInventoryTag == null) {
                isValid = false;
                validString = "'" + HEADER_INSTALLED + "' cannot be specified unless '"
                        + HEADER_ASSIGNED_INVENTORY_TAG + "' is specified";
                return new ValidInfo(isValid, validString);
            }
        } else {
            
            // must specify installation status with assigned inventory
            if (assignedInventoryTag != null) {
                isValid = false;
                validString = "Must specify '" + HEADER_INSTALLED + "' with assigned inventory item.";
                return new ValidInfo(isValid, validString);
            }
        }
        if (assignedInventoryTag != null) {
            List<String> cableTypeTags = cableTypeInventoryTagMap.get(catalogItem);
            if (cableTypeTags != null && cableTypeTags.contains(assignedInventoryTag)) {
                // tag name already in use for cable type
                isValid = false;
                validString = "Duplicate '" + HEADER_ASSIGNED_INVENTORY_TAG + "' value '" + assignedInventoryTag + "' specified in spreadsheet";
                return new ValidInfo(isValid, validString);
            }
            // handle assigned item
            ItemDomainCableDesignControllerUtility utility = new ItemDomainCableDesignControllerUtility();
            try {
                utility.updateAssignedInventory(entity, catalogItem, assignedInventoryTag, isInstalled, user);
            } catch (CdbException ex) {
                isValid = false;
                validString = "Error updating assigned item: " + ex.getMessage();
                return new ValidInfo(isValid, validString);
            }
            // update data structure for checking for duplicate tag name for cable type
            if (cableTypeTags == null) {
                cableTypeTags = new ArrayList<>();
                cableTypeInventoryTagMap.put(catalogItem, cableTypeTags);
            }
            cableTypeTags.add(assignedInventoryTag);            
        }
        
        // get endpoint items
        ItemDomainMachineDesign endpoint1Item = (ItemDomainMachineDesign) rowMap.get(KEY_ENDPOINT1_ITEM);
        ItemDomainMachineDesign endpoint2Item = (ItemDomainMachineDesign) rowMap.get(KEY_ENDPOINT2_ITEM);        
        
        String endpoint1PortName = (String) rowMap.get(KEY_ENDPOINT1_PORT);
        String endpoint2PortName = (String) rowMap.get(KEY_ENDPOINT2_PORT);
        String endpoint1ConnectorName = (String) rowMap.get(KEY_ENDPOINT1_CONNECTOR);
        String endpoint2ConnectorName = (String) rowMap.get(KEY_ENDPOINT2_CONNECTOR);
        
        // handle endpoint1 ======        
        // endpoint machine item must be specified
        if (endpoint1Item == null) {
            if (isValid) {
                // avoid duplicate warning if unable to find specified item (vs. not specifying one)
                isValid = false;
                validString = appendToString(
                        validString, "Endpoint1 must be specified.");
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
            isValid = false;
            validString = appendToString(
                    validString, "Endpoint2 must be specified.");
        } else {
            ValidInfo endpoint2ValidInfo =
                    entity.setEndpoint2Import(endpoint2Item, endpoint2PortName, endpoint2ConnectorName);
            if (!endpoint2ValidInfo.isValid()) {
                isValid = false;
                validString = appendToString(
                        validString, endpoint2ValidInfo.getValidString());
            }
        }
        
        // update data structures for checking duplicate port names
        if ((endpoint1PortName != null) && (!endpoint1PortName.isEmpty())) {
            addNameInUse(endpoint1Item, endpoint1PortName);
        }
        if ((endpoint2PortName != null) && (!endpoint2PortName.isEmpty())) {
            addNameInUse(endpoint2Item, endpoint2PortName);
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
