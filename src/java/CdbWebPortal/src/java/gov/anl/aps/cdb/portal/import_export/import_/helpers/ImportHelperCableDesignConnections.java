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
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.BooleanColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperCableDesignConnections 
        extends ImportHelperBase<ItemElementRelationship, ItemElementRelationshipController> {
    
    private static final String KEY_CABLE_ITEM = "importSecondItem";
    private static final String KEY_CABLE_END = "cableEndDesignation";
    private static final String KEY_IS_PRIMARY = "importPrimaryCableConnection";
    private static final String KEY_MACHINE_ITEM = "importFirstItem";
    private static final String KEY_PORT_NAME = "importFirstItemConnectorName";
    private static final String KEY_CONNECTOR_NAME = "importSecondItemConnectorName";
    
    private static final String LABEL_CABLE_ITEM = "Cable Design Item";
    private static final String LABEL_CABLE_END = "Cable End";
    private static final String LABEL_IS_PRIMARY = "Is Primary";
    private static final String LABEL_MACHINE_ITEM = "Machine Item";
    
    @Override
    protected List initColumnSpecs() {

        List<ColumnSpec> specs = new ArrayList<>();

        specs.add(existingItemIdColumnSpec());
        specs.add(deleteExistingItemColumnSpec());
        
        specs.add(new IdOrNameRefColumnSpec(
                LABEL_CABLE_ITEM,
                KEY_CABLE_ITEM,
                "setImportSecondItem",
                "Numeric ID or name of CDB cable design item. Name must be unique and prefixed with '#'.",
                "getSecondItem",
                "getSecondItem",
                ColumnModeOptions.rCREATErUPDATE(),
                ItemDomainCableDesignController.getInstance(),
                Item.class,
                ""));

        specs.add(new StringColumnSpec(
                LABEL_CABLE_END,
                KEY_CABLE_END,
                "setCableEndDesignation",
                "Specifies end of cable for connection, legal values are 1 and 2.",
                "getCableEndDesignation",
                ColumnModeOptions.rCREATErUPDATE(),
                0));
        
        specs.add(new BooleanColumnSpec(
                LABEL_IS_PRIMARY,
                KEY_IS_PRIMARY,
                "setImportPrimaryCableConnection",
                "True/yes if connection is primary for cable end, false/no otherwise. Primary connections cannot be created using this format, use cable design create format instead.",
                "isPrimaryCableConnection",
                ColumnModeOptions.oCREATEoUPDATE()));

        specs.add(new IdOrNameRefColumnSpec(
                LABEL_MACHINE_ITEM,
                KEY_MACHINE_ITEM,
                "setImportFirstItem",
                "Numeric ID or name of CDB machine item. Name must be unique and prefixed with '#'.",
                "getFirstItem",
                "getFirstItem",
                ColumnModeOptions.rCREATErUPDATE(),
                ItemDomainMachineDesignController.getInstance(),
                Item.class,
                ""));

        specs.add(new StringColumnSpec(
                "Machine Item Port Name",
                KEY_PORT_NAME,
                "setImportFirstItemConnectorName",
                "Name of connector for port on machine item.",
                "getFirstItemConnectorName",
                ColumnModeOptions.oCREATEoUPDATE(),
                0));

        specs.add(new StringColumnSpec(
                "Cable Connector Name",
                KEY_CONNECTOR_NAME,
                "setImportSecondItemConnectorName",
                "Name of cable connector for connection.",
                "getSecondItemConnectorName",
                ColumnModeOptions.oCREATEoUPDATE(),
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
    protected String getCreateMessageTypeName() {
        return "cable design connection";
    }
    
    @Override
    protected List<ItemElementRelationship> generateExportEntityList_() {
        List<ItemElementRelationship> entityList = new ArrayList<>();
        List<ItemDomainCableDesign> cableList = ItemDomainCableDesignController.getInstance().getExportEntityList();
        for (ItemDomainCableDesign cable : cableList) {
            entityList.addAll(cable.getCableRelationshipList());
        }
        return entityList;
    }
    
    private CreateInfo createUpdateEntityCommon(
            ItemElementRelationship existingRelationship, 
            Map<String, Object> rowMap) {
        
        boolean isValid = true;
        String validStr = "";
        ItemElementRelationship connectionRelationship = null;

        // extract column values
        ItemDomainCableDesign cableDesignItem = (ItemDomainCableDesign) rowMap.get(KEY_CABLE_ITEM);
        String cableEnd = (String) rowMap.get(KEY_CABLE_END);
        Boolean isPrimary = (Boolean) rowMap.get(KEY_IS_PRIMARY);
        ItemDomainMachineDesign machineItem = (ItemDomainMachineDesign) rowMap.get(KEY_MACHINE_ITEM);
        String machineItemPortName = (String) rowMap.get(KEY_PORT_NAME);
        String cableConnectorName = (String) rowMap.get(KEY_CONNECTOR_NAME);
        
        // common validation for create and update modes
        
        if (cableDesignItem == null) {
            // cable design must be specified
            isValid = false;
            validStr = LABEL_CABLE_ITEM + " must be specified.";
        }
        
        if (cableEnd == null) {
            // cable end must be specified
            isValid = false;
            validStr = appendToString(validStr, LABEL_CABLE_END + " must be specified.");

        }
        
        if (!CdbEntity.isValidCableEndDesignation(cableEnd)) {
            isValid = false;
            validStr = appendToString(validStr, "Invalid cable end value: " + cableEnd);
        }

        // check if port name is in use within spreadsheet
        if ((machineItemPortName != null) && (!machineItemPortName.isEmpty())) {
            if (nameInUse(machineItem, machineItemPortName)) {
                isValid = false;
                validStr = appendToString(
                        validStr,
                        "Duplicate use of port name: "
                        + machineItemPortName + " for same machine item in spreadsheet.");
            }
        }

        // check if connector name is in use within spreadsheet
        if ((cableConnectorName != null) && (!cableConnectorName.isEmpty())) {
            if (nameInUse(cableDesignItem, cableConnectorName)) {
                isValid = false;
                validStr = appendToString(
                        validStr,
                        "Duplicate use of cable connector name: "
                        + cableConnectorName + " for same cable in spreadsheet.");
            }
        }
        
        if (existingRelationship == null) {// create a new relationship
            
            if (isPrimary == null || isPrimary) {
                // isPrimary must be specified to be false for creating new connections
                isValid = false;
                validStr = appendToString(validStr,
                        LABEL_IS_PRIMARY + " must be specified to be false for creating new connections.");
            }
            
            // create default instance for validation table in case of error
            connectionRelationship
                    = ItemElementRelationshipController.getInstance().createEntityInstance();

            if (isValid) {
                // create new relationship and check result
                CreateInfo createInfo = cableDesignItem.setEndpointImport(
                        null,
                        machineItem,
                        machineItemPortName,
                        cableConnectorName,
                        cableEnd,
                        false);
                if (!createInfo.getValidInfo().isValid()) {
                    isValid = false;
                    validStr = appendToString(validStr, createInfo.getValidInfo().getValidString());
                } else {
                    if (createInfo.getEntity() == null) {
                        isValid = false;
                        validStr = "Unexpected error creating connection.";
                    } else {
                        connectionRelationship = (ItemElementRelationship) createInfo.getEntity();
                    }
                }
            }
                    
        } else { // existing relationship not null, update existing relationship

            connectionRelationship = existingRelationship;
            
            if ((cableDesignItem != null) && (!cableDesignItem.equals(connectionRelationship.getSecondItem()))) {
                // cable design item cannot be changed in update
                isValid = false;
                validStr = LABEL_CABLE_ITEM + " cannot be modified in update operation.";
            }

            // validate isPrimary flag
            if (isPrimary == null) {
                // isPrimary must be specified
                isValid = false;
                validStr = appendToString(validStr, LABEL_IS_PRIMARY + " must be specified.");

            } else if (isPrimary != connectionRelationship.isPrimaryCableConnection()) {
                // isPrimary cannot be changed in update
                isValid = false;
                validStr = appendToString(validStr, LABEL_IS_PRIMARY + " cannot be modified in update operation.");
            }

            // validate cable end
            if (connectionRelationship.isPrimaryCableConnection() 
                    && !cableEnd.equals(connectionRelationship.getCableEndDesignation())) {
                // cable end cannot be changed for primary connection
                isValid = false;
                validStr = appendToString(validStr, LABEL_CABLE_END + " cannot be changed for primary connection.");
            }

            // validate machine item
            if (machineItem == null) {
                // machine item must be specified
                isValid = false;
                validStr = appendToString(validStr, LABEL_MACHINE_ITEM + " must be specified.");
            }

            if (isValid) {
                CreateInfo updateInfo = cableDesignItem.setEndpointImport(
                        connectionRelationship, 
                        machineItem, 
                        machineItemPortName, 
                        cableConnectorName, 
                        cableEnd, 
                        connectionRelationship.isPrimaryCableConnection(),
                        connectionRelationship);
                if (!updateInfo.getValidInfo().isValid()) {
                    isValid = false;
                    validStr = appendToString(validStr, updateInfo.getValidInfo().getValidString());
                }
            }            
        }

        if (connectionRelationship != null) {

            // update data structures for checking duplicate names            
            if ((machineItemPortName != null) && (!machineItemPortName.isEmpty())) {
                addNameInUse(machineItem, machineItemPortName);
            }
            if ((cableConnectorName != null) && (!cableConnectorName.isEmpty())) {
                addNameInUse(cableDesignItem, cableConnectorName);
            }

            // set attributes in invalid entity for display in validation table
            if (!isValid) {
                connectionRelationship.setImportSecondItem(cableDesignItem);
                connectionRelationship.setImportFirstItem(machineItem);
                connectionRelationship.setCableEndDesignation(cableEnd);
                connectionRelationship.setImportFirstItemConnectorName(machineItemPortName);
                connectionRelationship.setImportSecondItemConnectorName(cableConnectorName);
            }
        }

        return new CreateInfo(connectionRelationship, isValid, validStr);
    }

    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        return createUpdateEntityCommon(null, rowMap);
    }
    
    @Override
    protected ValidInfo updateEntityInstance(ItemElementRelationship entity, Map<String, Object> rowMap) {
        CreateInfo updateInfo = createUpdateEntityCommon(entity, rowMap);
        return updateInfo.getValidInfo();
    }

    @Override
    protected ValidInfo validateDeleteEntityInstance(ItemElementRelationship entity, Map<String, Object> rowMap) {
        
        boolean isValid = true;
        String validStr = "";
        
        // don't allow deleting primary connection
        if (entity.isPrimaryCableConnection()) {
            isValid = false;
            validStr = "Deleting primary cable connection is not allowed.";
        }
        
        return new ValidInfo(isValid, validStr);
    }
}
