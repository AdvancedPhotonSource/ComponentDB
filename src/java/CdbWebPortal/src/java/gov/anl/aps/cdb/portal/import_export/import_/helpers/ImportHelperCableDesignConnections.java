/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemElementRelationshipController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author craig
 */
public class ImportHelperCableDesignConnections 
        extends ImportHelperBase<ItemElementRelationship, ItemElementRelationshipController> {
    
    private static final String KEY_CABLE_ITEM = "importSecondItem";
    private static final String KEY_CABLE_END = "cableEndDesignation";
    private static final String KEY_MACHINE_ITEM = "importFirstItem";
    private static final String KEY_PORT_NAME = "importFirstItemConnectorName";
    private static final String KEY_CONNECTOR_NAME = "importSecondItemConnectorName";
    
    private static final String LABEL_CABLE_ITEM = "Cable Design Item";
    
    private final Map<Item, Set<String>> itemNameMap = new HashMap<>();
    
    private boolean nameInUse(Item item, String name) {
        Set<String> names = itemNameMap.get(item);
        if (names == null) {
            return false;
        } else if (names.contains(name)) {
            return true;
        } else {
            return false;
        }
    }
    
    private void addNameInUse(Item item, String name) {
        Set<String> names = itemNameMap.get(item);
        if (names == null) {
            names = new HashSet<>();
            itemNameMap.put(item, names);
        }
        names.add(name);
    }

    @Override
    protected List getColumnSpecs() {

        List<ColumnSpec> specs = new ArrayList<>();

        specs.add(new IdOrNameRefColumnSpec(
                LABEL_CABLE_ITEM,
                KEY_CABLE_ITEM,
                "setImportSecondItem",
                "Numeric ID or name of CDB cable design item. Name must be unique and prefixed with '#'.",
                "getImportSecondItem",
                null,
                ColumnModeOptions.rCREATE(),
                ItemDomainCableDesignController.getInstance(),
                Item.class,
                ""));

        specs.add(new StringColumnSpec(
                "Cable End",
                KEY_CABLE_END,
                "setCableEndDesignation",
                "Specifies end of cable for connection, legal values are 1 and 2.",
                "setCableEndDesignation",
                ColumnModeOptions.rCREATE(),
                0));

        specs.add(new IdOrNameRefColumnSpec(
                "Machine Item",
                KEY_MACHINE_ITEM,
                "setImportFirstItem",
                "Numeric ID or name of CDB machine item. Name must be unique and prefixed with '#'.",
                "getImportFirstItem",
                null,
                ColumnModeOptions.rCREATE(),
                ItemDomainMachineDesignController.getInstance(),
                Item.class,
                ""));

        specs.add(new StringColumnSpec(
                "Machine Item Port Name",
                KEY_PORT_NAME,
                "setImportFirstItemConnectorName",
                "Name of connector for port on machine item.",
                "getImportFirstItemConnectorName",
                ColumnModeOptions.oCREATE(),
                0));

        specs.add(new StringColumnSpec(
                "Cable Connector Name",
                KEY_CONNECTOR_NAME,
                "setImportSecondItemConnectorName",
                "Name of cable connector for connection.",
                "getImportSecondItemConnectorName",
                ColumnModeOptions.oCREATE(),
                0));

        return specs;
    }

    @Override
    public ItemElementRelationshipController getEntityController() {
        return ItemElementRelationshipController.getInstance();
    }


    @Override
    public String getFilenameBase() {
        return "Cable Design Connections";
    }
    
    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        
        boolean isValid = true;
        String validStr = "";
        
        // extract column values
        ItemDomainCableDesign cableDesignItem = (ItemDomainCableDesign) rowMap.get(KEY_CABLE_ITEM);
        String cableEnd = (String) rowMap.get(KEY_CABLE_END);
        Item machineItem = (Item) rowMap.get(KEY_MACHINE_ITEM);
        String machineItemPortName = (String) rowMap.get(KEY_PORT_NAME);
        String cableConnectorName = (String) rowMap.get(KEY_CONNECTOR_NAME);
        
        // create default instance for validation table in case of error
        ItemElementRelationship connectionRelationship = 
                ItemElementRelationshipController.getInstance().createEntityInstance();
                
        // cable design item is required
        if (cableDesignItem == null) {
            isValid = false;
            // validStr = LABEL_CABLE_ITEM + " must be specified.";
            
        } else {
            // create cable relationshiop with specified parameters
            CreateInfo addRelInfo = cableDesignItem.addConnectionRelationshipImport(
                    machineItem, machineItemPortName, cableConnectorName, cableEnd, false);
            if (!addRelInfo.getValidInfo().isValid()) {
                isValid = false;
                validStr = addRelInfo.getValidInfo().getValidString();
            } else {
                if (addRelInfo.getEntity() == null) {
                    isValid = false;
                    validStr = "Unexpected error creating connection.";
                } else {
                    connectionRelationship = (ItemElementRelationship) addRelInfo.getEntity();
                    if (connectionRelationship != null) {
                        
                        // check if port name is in use within spreadsheet
                        if ((machineItemPortName != null) && (!machineItemPortName.isEmpty())) {
                            if (nameInUse(machineItem, machineItemPortName)) {
                                isValid = false;
                                validStr = "Duplicate use of port name: " + machineItemPortName + " for same machine item in spreadsheet.";
                            } else {
                               addNameInUse(machineItem, machineItemPortName);
                            }
                        }
                        
                        // check if connector name is in use within spreadsheet
                        if ((cableConnectorName != null) && (!cableConnectorName.isEmpty())) {
                            if (nameInUse(cableDesignItem, cableConnectorName)) {
                                isValid = false;
                                validStr = "Duplicate use of cable connector name: " 
                                        + cableConnectorName + " for same cable in spreadsheet.";
                            } else {
                                addNameInUse(cableDesignItem, cableConnectorName);
                            }
                        }
                        
                    }
                }
            }
        }

        if (connectionRelationship != null) {
            connectionRelationship.setImportSecondItem(cableDesignItem);
            connectionRelationship.setImportFirstItem(machineItem);
            connectionRelationship.setCableEndDesignation(cableEnd);
            connectionRelationship.setImportFirstItemConnectorName(machineItemPortName);
            connectionRelationship.setImportSecondItemConnectorName(cableConnectorName);
        }
        
        return new CreateInfo(connectionRelationship, isValid, validStr);
    }
}
